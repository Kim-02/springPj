package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public abstract class ApiException extends RuntimeException {
    private final Integer code;
    private final String message;

    // ApiState(enum)만으로 에러 코드 설정
    protected ApiException(ApiState codeEnum) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + this.code;
    }

    // ApiState(enum) + 커스텀 메시지
    protected ApiException(ApiState codeEnum, String message) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + message;
    }

    // 정수 코드만으로 설정
    protected ApiException(Integer code) {
        super();
        this.code = code;
        this.message = "error." + this.code;
    }

    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
