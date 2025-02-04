package com.dasolsystem.core.jwt.util;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.entity.SignUpJwt;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.ResponsesignInJwtDto;
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
    //refresh 토큰 조회용 id만 반환
    public Long getRefreshTokenId(String name){
        TokenAccesserDto tokenAccesserDto = TokenAccesserDto.builder()
                .token(generateJWT(name,RefreshTokenExpTime))
                .build();
        RedisJwtId redisid = RedisJwtId.builder()
                .jwtToken(tokenAccesserDto.getToken())
                .expiration(RefreshTokenExpTime)
                .build();
        redisJwtRepository.save(redisid);
        return redisid.getId();
    }
    public String getNewAccessToken(TokenIdAccesserDto tokenIdAccesserDto){
        //ID로 Redis에서 객체 조회
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.parseLong(tokenIdAccesserDto.getTokenId()));
            if(optionalRedisJwtId.isPresent()){
                RedisJwtId redisid = optionalRedisJwtId.get();
                //만료기간
                if(redisid.getExpiration().equals(0L)) throw new AuthFailException(ApiState.ERROR_602,"Expired Refresh Token");
                if(validateToken(redisid.getJwtToken()).equals(JwtCode.OK)){
                    return generateAccessToken(tokenIdAccesserDto.getName());
                }
                else{
                    throw new AuthFailException(ApiState.ERROR_602,"validate Error"+validateToken(redisid.getJwtToken()));
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

//    public void saveRefreshToken(signInJwtBuilderDto builderDto) {
//        SignUpJwt jwt = SignUpJwt.builder()
//                .username(builderDto.getUserName())
//                .rtoken(builderDto.getRtoken())
//                .build();
//        jwtRepository.save(jwt);
//    }

    //DB의 RToken과 비교하기 위해 username으로 RToken을 가져옴
    public ResponsesignInJwtDto getRefreshTokenByName(String username) {
        SignUpJwt responsejwt = jwtRepository.findByusername(username);
        return ResponsesignInJwtDto.builder()
                .rtoken(responsejwt.getRtoken())
                .build();
    }
}
