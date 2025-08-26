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
            "/api/auth/verify",

            //swagger
            "/swagger-ui/index.html",
            "/swagger-ui/favicon-16x16.png",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui.css",
            "/v3/api-docs/swagger-config",
            "/v3/api-docs"
    };
}
