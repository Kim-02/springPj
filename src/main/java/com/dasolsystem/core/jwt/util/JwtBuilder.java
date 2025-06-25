package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.jwt.dto.JwtRequestDto;
import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
//    String generateAccessToken(String emailId);
//    String generateAccessToken(String studentId, String role,String username);
//    String getNewAccessToken(TokenIdAccesserDto tokenIdAccesserDto);
//    Long getRefreshTokenId(String emailId);
//    JwtCode validateToken(String token);
//    Claims getAccessTokenPayload(String token);
//    String getRefreshTokenEmailId(String tokenId);
//    boolean isEnableJwtRedis(String token);

    String generateAccessJWT(JwtRequestDto requestDto);
    Long generateRefreshId(String studentId);
    String generateRefreshJWT(String studentId,String jti);
}
