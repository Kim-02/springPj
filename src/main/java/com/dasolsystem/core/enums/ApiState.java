package com.dasolsystem.core.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiState {
    OK(200),

    //InvalidToken
    ERROR_600(600),//인증관련 오류
    //Auth fail
    ERROR_700(700),

    //Db fail
    ERROR_500(500),

    //code
    ERROR_101(101), //코드오류

    //file
    ERROR_800(800)

    //예상치 못한 에러
    ,ERROR_UNKNOWN(100);



    private final Integer num;
    public int getNum(){return num;}
}
