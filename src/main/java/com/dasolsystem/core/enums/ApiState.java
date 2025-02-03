package com.dasolsystem.core.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiState {
    OK(200),

    //인증 에러
    ERROR_601(601),
    ERROR_602(602), //RefreshToken만료


    //로그인 에러
    ERROR_901(901); //



    private final Integer num;
    public int getNum(){return num;}
}
