package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException(ApiState state,String message) {
        super(state, message);
    }
}
