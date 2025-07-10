package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class DBFaillException extends ApiException {
    public DBFaillException(ApiState state,String message) {
        super(state, message);
    }
}
