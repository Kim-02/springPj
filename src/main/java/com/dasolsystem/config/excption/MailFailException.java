package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class MailFailException extends ApiException {
    public MailFailException(ApiState state, String message) {
        super(state, message);
    }
}
