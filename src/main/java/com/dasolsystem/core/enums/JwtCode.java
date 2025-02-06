package com.dasolsystem.core.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JwtCode {

    OK(200, ""),
    NOT_EQUAL(901, "error.901"),
    EMPTY_TOKEN(902, "error.902"),
    WRONG(903, "error.903"),
    EXPIRE(904, "error.904"),
    NOT_EXIST_CLAIMS(905, "error.905"),
    NOT_SUPPORT(909, "error.909"),
    UNKNOWN(910, "error.910"),
    EXPIRE_REFRESH(911, "error.911"),
    DO_NOT_HAVE_SYSTEM_ROLE(912, "error.912"),
    DO_NOT_HAVE_ROLE_CODE(913, "error.913"),
    NOT_SYSTEM_MANAGER(914, "error.914"),
    NO_COMPANY(916, "error.914"),
    ;

    private final Integer code;
    private final String messageCode;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return messageCode;
    }
}
