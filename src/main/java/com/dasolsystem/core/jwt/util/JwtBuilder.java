package com.dasolsystem.core.jwt.util;

import com.dasolsystem.core.auth.Enum.JwtCode;
import org.springframework.stereotype.Component;

@Component
public interface JwtBuilder {
    String generateJWT(String name,Long exptime);
    String generateAccessToken(String name);
    String generateRefreshToken(String name);
    JwtCode validateToken(String token);
}
