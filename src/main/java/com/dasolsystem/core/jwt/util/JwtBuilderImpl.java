package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.entity.SignUpJwt;
import com.dasolsystem.core.jwt.dto.ResponsesignInJwtDto;
import com.dasolsystem.core.jwt.dto.signInJwtBuilderDto;
import com.dasolsystem.core.jwt.repository.JwtRepository;
import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtBuilderImpl implements JwtBuilder {

    @Autowired
    private JwtRepository jwtRepository;

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
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)), SignatureAlgorithm.HS256)
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
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)))
                    .build()
                    .parseClaimsJws(token);
            return JwtCode.OK;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            return JwtCode.WRONG;
        } catch (ExpiredJwtException e) {
            return JwtCode.EXPIRE;
        } catch (UnsupportedJwtException e) {
            return JwtCode.NOT_SUPPORT;
        } catch (IllegalArgumentException e) {
            return JwtCode.NOT_EXIST_CLAIMS;
        }
    }

    public void saveRefreshToken(signInJwtBuilderDto builderDto) {
        SignUpJwt jwt = SignUpJwt.builder()
                .username(builderDto.getUserName())
                .rtoken(builderDto.getRtoken())
                .build();
        jwtRepository.save(jwt);
    }

    //DB의 RToken과 비교하기 위해 username으로 RToken을 가져옴
    public ResponsesignInJwtDto getRefreshTokenByName(String username) {
        SignUpJwt responsejwt = jwtRepository.findByusername(username);
        return ResponsesignInJwtDto.builder()
                .rtoken(responsejwt.getRtoken())
                .build();
    }
}
