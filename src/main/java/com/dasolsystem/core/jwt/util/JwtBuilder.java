package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.jwt.dto.ResponsesignInJwtDto;
import com.dasolsystem.core.jwt.dto.signInJwtBuilderDto;
import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
    String generateJWT(String name,Long exptime);
    String generateAccessToken(String name);
    String generateRefreshToken(String name);
    JwtCode validateToken(String token);
    void saveRefreshToken(signInJwtBuilderDto builderDto);
    ResponsesignInJwtDto getRefreshTokenByName(String username);
}
