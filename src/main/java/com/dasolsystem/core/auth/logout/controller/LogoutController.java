package com.dasolsystem.core.auth.logout.controller;

import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.handler.ResponseJson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static com.dasolsystem.core.jwt.filter.JwtRequestFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class LogoutController {

    private final RedisJwtRepository redisJwtRepository;
    private final RedisTemplate<Object, Object> redisTemplate;
    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.
    @PostMapping("/logout")
    public ResponseEntity<ResponseJson<Object>> logout(HttpServletRequest request) {
        // 1) 헤더 유효성 검증
        String refreshHeader = request.getHeader("rAuthorization");
        String accessHeader  = request.getHeader("Authorization");
        if (!StringUtils.hasText(refreshHeader) || !refreshHeader.startsWith(BEARER_PREFIX) ||
                !StringUtils.hasText(accessHeader)  || !accessHeader.startsWith(BEARER_PREFIX)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseJson.builder()
                            .status(400)
                            .message("Invalid or missing Authorization headers")
                            .build());
        }

        // 2) Bearer 접두사 제거
        String refreshIdStr = refreshHeader.substring(BEARER_PREFIX.length());
        String accessToken  = accessHeader.substring(BEARER_PREFIX.length());

        // 3) 리프레시 토큰(ID) 파싱 및 삭제
        Long refreshId;
        try {
            refreshId = Long.valueOf(refreshIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseJson.builder()
                            .status(400)
                            .message("Invalid refresh token ID format")
                            .build());
        }

        // 삭제할 레코드가 있으면 제거
        boolean existed = redisJwtRepository.findById(refreshId)
                .map(r -> {
                    redisJwtRepository.deleteById(refreshId);
                    return true;
                })
                .orElse(false);

        if (!existed) {
            // 보안상 idempotent 하게 OK를 내려주거나, 명확히 실패를 알릴 수도 있습니다.
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseJson.builder()
                            .status(401)
                            .message("Refresh token not found or already invalidated")
                            .build());
        }

        // 4) 액세스 토큰 블랙리스트 등록
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue()
                        .set("blacklist:access:" + accessToken,
                                "logout",
                                Duration.ofMillis(ttl));
            }
        } catch (JwtException ignored) {
            // 이미 만료됐거나 변조된 토큰은 무시하고 계속 처리
        }

        // 5) 스프링 시큐리티 컨텍스트 클리어
        SecurityContextHolder.clearContext();

        // 6) 성공 응답
        return ResponseEntity
                .ok(ResponseJson.builder()
                        .status(200)
                        .message("Logged out successfully")
                        .build());
    }
}
