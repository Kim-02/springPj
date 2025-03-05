package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class DBFaillException extends RuntimeException {
    Integer code;
    String message;

    public DBFaillException(ApiState codeEnum) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + this.code;
    }

    public DBFaillException(ApiState codeEnum,String message) {
        super();
        this.code = codeEnum.getNum();
        this.message = "error." + message;
    }

    public DBFaillException(Integer codeEnum) {
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
