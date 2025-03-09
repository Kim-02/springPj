package com.dasolsystem.core.auth.vo;

import lombok.*;


@Getter
public class WhiteListVO {
    private final String[] WhiteList={
            "/api/signin",
            "/api/auth/login",
            "/api/logout",
            "/api/auth/signup",
            "/",
            "/api/press",
            "/api/likes",

            "/favicon.ico",

            "/test/signup",
            "/test/main",
            "/test/login",
            "/index.html",
            "/test/print",



            "/api/users/upload",
            "/api/amount/update",
            "/api/users/personal_upload",
            "/api/users/userdata",
            "/api/users/deleteuser",
            "/api/amount/download"
    };
}
