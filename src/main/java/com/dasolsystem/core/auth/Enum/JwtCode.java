package com.dasolsystem.core.auth.Enum;

public enum JwtCode {
    ACCESS,  // 유효한 토큰
    EXPIRED, // 만료된 토큰
    DENIED //유효하지 않은 토큰
}
