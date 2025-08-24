package com.dasolsystem.core.auth.vo;

import lombok.*;


@Getter
public class WhiteListVO {
    private final String[] WhiteList={
            "/",
            "/api/auth/logout",
            "/api/auth/login",
            "/api/auth/signup",
            "/index.html",
            "/api/ping",
            "/api/auth/verify"
    };
}
