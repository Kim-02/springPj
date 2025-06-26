package com.dasolsystem.core.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiState {
    OK(200),

    //인증 에러
    ERROR_600(600),//인증관련 오류
    ERROR_601(601),
    ERROR_602(602), //RefreshToken만료
    ERROR_603(603), //리프레시 토큰 식별자 불일치
    ERROR_604(604), //새 Refresh 토큰 생성 실패
    ERROR_605(605), //토큰 불일치
    ERROR_606(606), //로그아웃된 토큰
    //회원가입 에러
    ERROR_701(701), //Exist Users

    ERROR_801(801), //fileupload error

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
