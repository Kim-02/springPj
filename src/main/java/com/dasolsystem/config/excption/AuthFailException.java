package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;
import org.aspectj.bridge.MessageUtil;

public class AuthFailException extends RuntimeException {
    Integer code;
    String message;

    public AuthFailException(ApiState codeEnum) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + this.code;
    }

    public AuthFailException(ApiState codeEnum,String message) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + message;
    }

    public AuthFailException(Integer codeEnum) {
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
