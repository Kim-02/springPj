package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
    String generateJWT(String name,Long exptime, String role);
    String generateAccessToken(String name);
    String getNewAccessToken(TokenIdAccesserDto tokenIdAccesserDto);
    Long getRefreshTokenId(String name);
    JwtCode validateToken(String token);
    String getAccessTokenPayload(String token);
//    void saveRefreshToken(signInJwtBuilderDto builderDto);
//    ResponsesignInJwtDto getRefreshTokenByName(String username);
}
