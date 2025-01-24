package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.auth.Enum.JwtCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtBuilderImpl implements JwtBuilder {
    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.
    private static final Long AccessTokenExpTime = 1000*60L*3L;
    private static final Long RefreshTokenExpTime = 60L * 1000 * 60L;
    public String generateJWT(String name,Long exptime){
        Map<String,Object> header = new HashMap<>();
        header.put("typ", "JWT"); //토큰 헤더 설정

        Date ext = new Date();
        ext.setTime(ext.getTime()+exptime); //유효시간 설정

        Map<String,Object> payload = new HashMap<>();
        payload.put("user_name",name);//토큰 페이로드설정

        String jwt = Jwts.builder()
                .setHeader(header)
                .setClaims(payload)
                .setSubject("test")
                .setExpiration(ext)
                .signWith(SignatureAlgorithm.HS256,SecretKey.getBytes())
                .compact();
        return jwt;
    }
    public String generateAccessToken(String name){
        return generateJWT(name,AccessTokenExpTime);
    }
    public String generateRefreshToken(String name){
        return generateJWT(name,RefreshTokenExpTime);
    }

    public JwtCode validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return JwtCode.DENIED; // 토큰이 유효하지 않음
        }
        try {
            Jwts.parserBuilder().setSigningKey(SecretKey).build().parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) { // 기한 만료
            return JwtCode.EXPIRED;
        } catch (Exception e) {
            return JwtCode.DENIED;
        }
    }
}
