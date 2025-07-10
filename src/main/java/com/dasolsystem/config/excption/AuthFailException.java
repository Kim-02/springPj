package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;
import org.aspectj.bridge.MessageUtil;

public class AuthFailException extends ApiException {
    public AuthFailException(ApiState state,String message) {
        super(state, message);
    }
}
