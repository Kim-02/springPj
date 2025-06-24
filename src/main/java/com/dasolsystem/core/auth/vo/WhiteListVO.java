package com.dasolsystem.core.auth.vo;

import lombok.*;


@Getter
public class WhiteListVO {
    private final String[] WhiteList={
            "/",
            "/api/auth/logout",
            "/api/auth/login",
            "/api/auth/signup",
            "/api/users/file/upload",
            "/api/users/personal/upload",
            "/api/users/personal/find/id",
            "/api/users/personal/find/data",
            "/api/deposit/personal/update",
            "/api/users/personal/delete/user",
            "/api/users/personal/update/role",
            "/api/deposit/file/update",
            "/api/deposit/find_deposit/download",
            "/api/deposit/personal/refund",
            "/api/expend/post",
            "/api/amount/download/amount_check",
            "/api/announce/post"
    };
}
