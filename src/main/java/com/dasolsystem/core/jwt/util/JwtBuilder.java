package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.enums.JwtCode;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
    String generateJWT(String emailId, Long exptime, String role);
    String generateAccessToken(String emailId);
    String getNewAccessToken(TokenIdAccesserDto tokenIdAccesserDto);
    Long getRefreshTokenId(String emailId);
    JwtCode validateToken(String token);
    Claims getAccessTokenPayload(String token);
    String getRefreshTokenEmailId(String tokenId);
    boolean isEnableJwtRedis(String token);
}
