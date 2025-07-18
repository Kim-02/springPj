package com.dasolsystem.core.guardian;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.config.excption.InvalidTokenException;
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
        if(accessStatus == JwtCode.OK) {
            return TokenResponseDto.builder()
                    .jwtCode(JwtCode.OK)
                    .refreshToken(refreshTokenId)
                    .accessToken(accessToken)
                    .build();
        }
        if(accessStatus == JwtCode.EXPIRE){
            String refreshToken = redisJwtRepository.findById(Long.valueOf(refreshTokenId)).orElseThrow(
                    () -> new DBFaillException(ApiState.ERROR_500,"DB fail ,not exist Id")
            ).getJwtToken();
            log.info("-> get refresh token from redis");
            //기존 사용하던 리프레시 토큰을 사용할 수 없게 한다.
            String studentId;
            try{
                Claims refreshClaims = Jwts.parserBuilder()
                        .setAllowedClockSkewSeconds(30)
                        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(RefreshSecretKey)))
                        .build()
                        .parseClaimsJws(refreshToken).getBody();
                studentId = refreshClaims.getSubject();
            }catch (JwtException e){
                throw new InvalidTokenException(ApiState.ERROR_101,"refresh token expired");
            }
            redisJwtRepository.deleteById(Long.valueOf(refreshTokenId));
            log.info("-> delete refresh token from redis");

            log.info("--> get refresh token subject = "+studentId);
            Claims accessClaims;
            try{
                accessClaims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                        .build()
                        .parseClaimsJws(accessToken).getBody();
            }catch (ExpiredJwtException ex) {
                // 만료된 토큰이라도 ex.getClaims() 로 클레임 추출 가능
                accessClaims = ex.getClaims();
            }

            //여기서 새로운 아이디를 생성해서 반환한다.
            Long newRefreshTokenId = jwtBuilder.generateRefreshId(studentId);

            String newAccessToken = jwtBuilder.generateAccessJWT(
                    JwtRequestDto.builder()
                            .name(accessClaims.get("userName", String.class))
                            .role(accessClaims.get("role", String.class))
                            .studentId(studentId)
                            .build()
            );
            return TokenResponseDto.builder()
                    .jwtCode(JwtCode.OK)
                    .accessToken(newAccessToken)
                    .refreshToken(String.valueOf(newRefreshTokenId))
                    .build();
        }
        return TokenResponseDto.builder()
                .jwtCode(accessStatus)
                .message("Access token vaild failer")
                .refreshToken(null)
                .accessToken(null)
                .build();
    }

    public String getStudentId(String accessToken) {
        Claims accessClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                .build()
                .parseClaimsJws(accessToken).getBody();
        return accessClaims.getSubject();
    }

    public JwtCode validateToken(String token) {
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
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring(7);
        byte[] KeyBytes = Decoders.BASE64.decode(SecretKey);
        Key key = Keys.hmacShaKeyFor(KeyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();

    }

    public Boolean userValidate(HttpServletRequest request, String approvalRole) {
        Claims accessClaims = getServletTokenClaims(request);
        String roleName = accessClaims.get("role", String.class);
        Role role = Role.valueOf(roleName);
        return role.isAtLeast(Role.valueOf(approvalRole));
    }
}
