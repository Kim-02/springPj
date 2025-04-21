package com.dasolsystem.core.auth.logout.controller;

import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.handler.ResponseJson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;

import java.util.Optional;

import static com.dasolsystem.core.jwt.filter.JwtRequestFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class logoutController {

    private final RedisJwtRepository redisJwtRepository;

    @PostMapping("/logout")
    public ResponseEntity<ResponseJson<Object>> logout(HttpServletRequest request) {
        String token = request.getHeader("rAuthorization").substring(BEARER_PREFIX.length());
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.valueOf(token));
        if(optionalRedisJwtId.isPresent()) {
            SecurityContextHolder.clearContext(); //인증정보 삭제
            redisJwtRepository.deleteById(Long.valueOf(token));
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("logged out successfully")
                            .build()
            );
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(401)
                        .message("logged out failed")
                        .build()
        );
    }
}
