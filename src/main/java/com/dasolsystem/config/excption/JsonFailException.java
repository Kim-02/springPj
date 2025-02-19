package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class JsonFailException extends RuntimeException {
    Integer code;
    String message;

    public JsonFailException(ApiState codeEnum, String message) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + message;
    }
    public JsonFailException(ApiState codeEnum) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + this.code;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
