package com.dasolsystem.core.auth.vo;

import lombok.*;


@Getter
public class WhiteListVO {
    private final String[] WhiteList={
            "/api/signin",
            "/api/login",
            "/api/logout",
            "/api/sign-up",
            "/",
            "/api/press",
            "/api/likes",

            "/favicon.ico",

            "/test/signup",
            "/test/main",
            "/test/login",
            "/index.html",
            "/test/print"
    };
}
