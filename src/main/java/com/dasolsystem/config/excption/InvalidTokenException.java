package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class InvalidTokenException extends RuntimeException {
    Integer code;
    String message;

    public InvalidTokenException(ApiState codeEnum) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + this.code;
    }

    public InvalidTokenException(ApiState codeEnum,String message) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + message;
    }

    public InvalidTokenException(Integer codeEnum) {
        super();
        this.code = codeEnum;
        this.message = "error." + this.code;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
