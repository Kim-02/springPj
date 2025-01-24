package com.dasolsystem.core.jwt.util;

import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
    String generateJWT(String name,Long exptime);
    String generateAccessToken(String name);
    String generateRefreshToken(String name);
}
