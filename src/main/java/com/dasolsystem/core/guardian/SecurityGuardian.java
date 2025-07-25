package com.dasolsystem.core.guardian;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.enums.Role;
import com.dasolsystem.core.jwt.dto.JwtRequestDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityGuardian {
    /**
     * 받아온 키(토큰) 값과 리프레쉬 토큰 값을 변수에 넣음
     */
    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.
    @Value("${jwt.refresh.secret.key}")
    private String RefreshSecretKey;

    private final RedisJwtRepository redisJwtRepository;
    private final JwtBuilder jwtBuilder;
    /**
     *
     * @param accessToken 접두사를 제거한 엑세스 토큰
     * @param refreshTokenId 접두사를 제거한 리프레시 토큰 아이디
     * @return 리프레시 토큰과 엑세스를 포함하여 jwt코드 반환(만약 인증 실패라면 null반환)
     */
    public TokenResponseDto tokenValidator(String accessToken,String refreshTokenId) {
        JwtCode accessStatus = validateToken(accessToken);

        //토큰이 유효할때 -> 그대로 전송함
        if(accessStatus == JwtCode.OK) {
            return TokenResponseDto.builder()
                    .jwtCode(JwtCode.OK)
                    .refreshToken(refreshTokenId)
                    .accessToken(accessToken)
                    .build();
        }
        //토큰이 만료됐을 떄 -> 새로운 리프래쉬 토큰을 발급받아서 보냄
        if(accessStatus == JwtCode.EXPIRE){
            // redis에 있는 리프레시 토큰 추출한다.
            String refreshToken = redisJwtRepository.findById(Long.valueOf(refreshTokenId)).orElseThrow(
                    () -> new DBFaillException(ApiState.ERROR_500,"DB fail ,not exist Id")
            ).getJwtToken();
            log.info("-> get refresh token from redis");
            //기존 사용하던 리프레시 토큰을 사용할 수 없게 한다.
            redisJwtRepository.deleteById(Long.valueOf(refreshTokenId));
            //리프레쉬 토큰에 payload부분을 인출함
            Claims refreshClaims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(RefreshSecretKey)))
                    .build()
                    .parseClaimsJws(refreshToken).getBody();

            // payload에 sub 즉 학번을 가져옴
            String studentId = refreshClaims.getSubject();
            log.info("--> get refresh token subject = "+studentId);

            // 엑세스 토큰에 payload 부분을 가져옴
            Claims accessClaims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                    .build()
                    .parseClaimsJws(accessToken).getBody();

            //여기서 새로운 아이디를 생성해서 반환한다.
            Long newRefreshTokenId = jwtBuilder.generateRefreshId(studentId);

            String newAccessToken = jwtBuilder.generateAccessJWT(
                    JwtRequestDto.builder()
                            .name(accessClaims.get("userName", String.class))
                            .role(accessClaims.get("role", String.class))
                            .studentId(studentId)
                            .build()
            );
            // 새로 만든 토큰을 리턴함
            return TokenResponseDto.builder()
                    .jwtCode(JwtCode.OK)
                    .accessToken(newAccessToken)
                    .refreshToken(String.valueOf(newRefreshTokenId))
                    .build();
        }

        // if문에 걸리지 않았을떄는 오류가 있는 상태이기 때문에 null값을 보내준다.
        return TokenResponseDto.builder()
                .jwtCode(accessStatus)
                .message("Access token vaild failer")
                .refreshToken(null)
                .accessToken(null)
                .build();
    }

    /**
     * 엑세스 토큰을 이용해 Claim을 추출하고 Claim에서 학번 추출하는 함수임
     * @param accessToken 학번 싸게 엑세스 토큰임
     * @return Claim에서 학번 반환
     */
    public String getStudentId(String accessToken) {
        Claims accessClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                .build()
                .parseClaimsJws(accessToken).getBody();
        return accessClaims.getSubject();
    }

    /**
     * 들어오는 토큰의 유효성을 검사하고, 결과를 JwtCode로 반환
     *
     * @param token 유효성을 검사할 토큰
     * @return 토큰이 유효하면 JwtCode.OK, 만료됐으면 JwtCode.EXPIRE, 그 외는 JwtCode.WRONG 반환
     */
    public JwtCode  validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                    .build()
                    .parseClaimsJws(token);
            return JwtCode.OK;
        }catch(ExpiredJwtException e){
            log.warn("token expired{}", e.getMessage());
            return JwtCode.EXPIRE;
        }catch(SecurityException | MalformedJwtException e){
            log.error("token is invalid{}", e.getMessage());
            return JwtCode.WRONG;
        }catch(UnsupportedJwtException e){
            log.error("token is Not supported{}", e.getMessage());
            return JwtCode.WRONG;
        }catch(IllegalArgumentException e){
            log.error("token is empty{}", e.getMessage());
            return JwtCode.WRONG;
        }
    }

    /**
     * HttpSevletRequest로 받은 토큰을 처리할 수 있는 요청
     * Bearer을 포함한 원문을 보내야 한다.
     * @param request
     * @return Claims를 반환
     */
    public Claims getServletTokenClaims(HttpServletRequest request) {
        // Header부분 가져옴
        String header = request.getHeader("Authorization");
        //Header가 비어있거나 Bearer로 시작하지 않으면 null값 보냄
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        // 앞에 7글자 제외함 "Brearer "제외
        String token = header.substring(7);
        byte[] KeyBytes = Decoders.BASE64.decode(SecretKey);
        Key key = Keys.hmacShaKeyFor(KeyBytes);

        // payload부분 추출해서 전송
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();

    }

    /**
     * 사이트에서 접근 권한이 필요할떄 접근 권한을 검사한다.
     *
     * @param request 클라이언트의 요청 객체
     * @param approvalRole 서비스에 필요한 최소한의 권한
     * @return 필요한 권한 레벨보다 요청 객체의 권한 레벨이 높다면 T 아니면 F
     */
    public Boolean userValidate(HttpServletRequest request, String approvalRole) {
        Claims accessClaims = getServletTokenClaims(request);
        String roleName = accessClaims.get("role", String.class);
        Role role = Role.valueOf(roleName);
        return role.isAtLeast(Role.valueOf(approvalRole));
    }
}
