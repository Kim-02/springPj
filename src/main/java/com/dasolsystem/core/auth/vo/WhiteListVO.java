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

            "/deposit",
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
            "/api/amount/download",
            "/api/amount/personal/update",
            "/api/users/updateuser",
            "/api/users/personal_upload",
            "/api/users/finduserid",
            "/api/amount/userdata",
            "/api/amount/refund",
            "/api/expend/update",
            "/check_amount/findexpender",

            "/api/deposit/file/update",
            "/api/deposit/personal/update",
            "/api/amount/download/amount_check"
    };
}
