package com.dasolsystem.core.jwt.util;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.TokenAccesserDto;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import com.dasolsystem.core.jwt.repository.JwtRepository;
import com.dasolsystem.core.jwt.repository.RedisJwtRepository;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtBuilderImpl implements JwtBuilder {
    @Autowired
    private JwtRepository jwtRepository;

    @Autowired
    private RedisJwtRepository redisJwtRepository;

    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.
    private static final Long AccessTokenExpTime = 1000*60L*3L;
    private static final Long RefreshTokenExpTime = 60L * 1000 * 60L;
    private static final String Defult_Role = "ROLE_USER";
    public String generateJWT(String emailId,Long exptime,String role){
        Map<String,Object> header = new HashMap<>();
        header.put("typ", "JWT"); //토큰 헤더 설정

        Date ext = new Date();
        ext.setTime(ext.getTime()+exptime); //유효시간 설정

        Map<String,Object> payload = new HashMap<>();
        payload.put("EmailId",emailId);//토큰 페이로드설정
        payload.put("role", role);
        String jwt = Jwts.builder()
                .setHeader(header)
                .setClaims(payload)
                .setSubject("test")
                .setExpiration(ext)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecretKey)), SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }
    public String generateAccessToken(String emailId){
        return generateJWT(emailId,AccessTokenExpTime, Defult_Role);
    }
    //refresh 토큰 조회용 id만 반환
    public Long getRefreshTokenId(String emailId){
        TokenAccesserDto tokenAccesserDto = TokenAccesserDto.builder()
                .token(generateJWT(emailId,RefreshTokenExpTime, Defult_Role))
                .build();
        RedisJwtId redisid = RedisJwtId.builder()
                .jwtToken(tokenAccesserDto.getToken())
                .expiration(RefreshTokenExpTime)
                .build();
        redisJwtRepository.save(redisid);
        return redisid.getId();
    }
    //payload 에서 정보를 얻기 위함
    public Claims getAccessTokenPayload(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String getRefreshTokenEmailId(String tokenId){
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.parseLong(tokenId));
        if(optionalRedisJwtId.isPresent()){
            RedisJwtId redisid = optionalRedisJwtId.get();
            if(validateToken(redisid.getJwtToken()).equals(JwtCode.OK)){ //refresh 토큰의 유효성 검사
                return getAccessTokenPayload(redisid.getJwtToken()).get("EmailId").toString();
            }
            else{
                throw new AuthFailException(ApiState.ERROR_602,"validate Error.ErrorCode."+validateToken(redisid.getJwtToken()));
            }
        }
        else{
            throw new AuthFailException(ApiState.ERROR_602,"None id in Redis. Login");
        }
    }
    public String getNewAccessToken(TokenIdAccesserDto tokenIdAccesserDto){
        //ID로 Redis에서 객체 조회
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.parseLong(tokenIdAccesserDto.getTokenId()));
            if(optionalRedisJwtId.isPresent()){
                RedisJwtId redisid = optionalRedisJwtId.get();
                if(validateToken(redisid.getJwtToken()).equals(JwtCode.OK)){ //refresh 토큰의 유효성 검사
                    return generateAccessToken(tokenIdAccesserDto.getEmailId());
                }
                else{
                    throw new AuthFailException(ApiState.ERROR_602,"validate Error.ErrorCode."+validateToken(redisid.getJwtToken()));
                }
            }
            else{
                throw new AuthFailException(ApiState.ERROR_602,"None id in Redis");
            }

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

    public boolean isEnableJwtRedis(String token){
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.valueOf(token));
        if(optionalRedisJwtId.isPresent()) {
            return true;
        }
        return false;
    }

}
