package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class FileException extends ApiException {
    public FileException(ApiState state, String message) {
        super(state, message);
    }
}
