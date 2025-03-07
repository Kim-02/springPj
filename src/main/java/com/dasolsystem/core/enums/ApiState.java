package com.dasolsystem.core.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiState {
    OK(200),

    //인증 에러
    ERROR_601(601),
    ERROR_602(602), //RefreshToken만료

    //회원가입 에러
    ERROR_701(701), //Exist Users



    //로그인 에러
    ERROR_901(901), //

    //DB 관련 오류
    ERROR_501(501), //DB접근 오류
    ERROR_502(502), //DB탐색 오류
    ERROR_503(503) //DB에 이미 있는 데이터

    //예상치 못한 에러
    ,ERROR_UNKNOWN(100);

    private final Integer num;
    public int getNum(){return num;}
}
