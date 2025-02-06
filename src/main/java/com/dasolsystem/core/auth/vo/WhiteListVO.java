package com.dasolsystem.core.auth.vo;

import lombok.*;


@Getter
public class WhiteListVO {
    private final String[] WhiteList={
            "/api/signin",
            "/api/login",
            "/api/sign-up",
            "/",

            "/test/signup",
            "/test/main",
            "/test/login",
            "/index.html"
    };
}
