package com.dasolsystem.core.jwt.util;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.JwtRequestDto;
import com.dasolsystem.core.jwt.repository.JwtRepository;
import com.dasolsystem.core.redis.reopsitory.RedisJwtRepository;
import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
@RequiredArgsConstructor
public class JwtBuilderImpl implements JwtBuilder {
    private final RedisJwtRepository redisJwtRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.

    @Value("${jwt.refresh.secret.key}")
    private String RefreshSecretKey;

    private static final Long AccessTokenExpTime = Duration.ofMinutes(5).toMillis();
    private static final Long RefreshTokenExpTime = Duration.ofDays(10).toMillis();

    /**
     * Access 토큰을 발급하는 것에 사용
     */
    public String generateAccessJWT(JwtRequestDto requestDto) {
        Date ext = new Date();
        ext.setTime(ext.getTime()+AccessTokenExpTime); //유효시간 설정

        Map<String,Object> payload = new HashMap<>();
        payload.put("role", requestDto.getRole());
        payload.put("userName",requestDto.getName());
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(payload)
                .setSubject(requestDto.getStudentId())
                .setExpiration(ext)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)), HS256)
                .compact();
    }
    public String generateRefreshJWT(String studentId,String jti) {
        Date ext = new Date(System.currentTimeMillis() + RefreshTokenExpTime);
        return Jwts.builder()
                .setId(jti)
                .setHeaderParam("typ", "RJWT")
                .setSubject(studentId)
                .setExpiration(ext)
                .signWith(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(RefreshSecretKey)
                ), HS256)
                .compact();
    }
    /**
     * Refresh 토큰 발급에 사용된다.
     */
    public Long generateRefreshId(String studentId) {
        String key = "refresh:" + studentId;

        // 1) 기존 JTI 조회
        String existingJti = redisTemplate.opsForValue().get(key);
        if (existingJti != null) {
            // 2) Redis 키 삭제
            redisTemplate.delete(key);
            // 3) DB 레코드 삭제 (findByJti 후 delete)
            redisJwtRepository.findByJti(existingJti)
                    .ifPresent(redisJwtRepository::delete);
        }

        // 4) 새 JTI 생성
        String newJti = UUID.randomUUID().toString();
        // 5) Redis에 저장 (키 갱신)
        redisTemplate.opsForValue().set(key, newJti, Duration.ofMillis(RefreshTokenExpTime));

        // 6) 새 리프레시 토큰 생성
        String token = generateRefreshJWT(studentId, newJti);

        // 7) DB에 저장
        RedisJwtId redisJwtId = RedisJwtId.builder()
                .jti(newJti)
                .jwtToken(token)
                .ttl(RefreshTokenExpTime)
                .build();
        RedisJwtId saved = redisJwtRepository.save(redisJwtId);

        return saved.getId();
    }
}
