package com.dasolsystem.core.jwt.controller;

import com.dasolsystem.config.excption.InvalidTokenException;
import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.JwtRequestDto;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final RedisJwtRepository redisJwtRepository;
    private final JwtBuilder jwtBuilder;
    @Value("${jwt.refresh.secret.key}")
    private String RefreshSecretKey;
    @Value("${jwt.secret.key}")
    private String SecretKey;

    @PostMapping("/refresh")
    @Transactional
    public String verificationAndRefreshToken(@RequestHeader("rAuthorization") String refreshId,
                                              @RequestHeader("Authorization") String token,
                                              HttpServletResponse rs) {
        try {
            // 1) 클라이언트가 보낸 refresh 토큰 ID로 DB에서 실제 토큰 문자열 조회
            RedisJwtId stored = redisJwtRepository.findById(Long.valueOf(refreshId))
                    .orElseThrow(() -> new InvalidTokenException(ApiState.ERROR_600, "유효하지 않은 Refresh 토큰 ID"));

            String storedRefreshToken = stored.getJwtToken();

            // 2) Refresh 토큰 서명·만료 검증
            Claims refreshClaims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(RefreshSecretKey)))
                    .build()
                    .parseClaimsJws(storedRefreshToken)
                    .getBody();

            // 3) Access 토큰이 만료됐더라도 페이로드만 꺼내오기 (ExpiredJwtException 활용)
            Claims accessClaims;
            try {
                accessClaims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (ExpiredJwtException e) {
                accessClaims = e.getClaims();
            }
            String studentId = refreshClaims.getSubject();
            if(!accessClaims.getSubject().equals(studentId))
                throw new InvalidTokenException(ApiState.ERROR_101,"토큰 인증 값이 다릅니다.");
            // 4) 새로운 Refresh 토큰 ID 발급 및 저장
            Long newRefreshId = jwtBuilder.generateRefreshId(studentId);
            RedisJwtId newStored = redisJwtRepository.findById(newRefreshId)
                    .orElseThrow(() -> new InvalidTokenException(ApiState.ERROR_101, "새 Refresh 토큰 생성 실패"));

            String newRefreshTokenId = newStored.getId().toString();

            // 5) 새로운 Access 토큰 생성
            String newAccessToken = jwtBuilder.generateAccessJWT(
                    JwtRequestDto.builder()
                            .role(accessClaims.get("role", String.class))
                            .name(accessClaims.get("userName", String.class))
                            .studentId(studentId)
                            .build()
            );

            // 6) 헤더에 새 토큰 세팅
            rs.setHeader("Authorization", newAccessToken);
            rs.setHeader("rAuthorization", newRefreshTokenId);

            // 7) 성공 상태 반환
            return ApiState.OK.name();

        } catch (JwtException | IllegalArgumentException e) {
            // 서명 실패 · 만료 등 처리
            throw new InvalidTokenException(ApiState.ERROR_600, e.getMessage());
        }
    }
}
