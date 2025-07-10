package com.dasolsystem.config.excption;

import com.dasolsystem.core.enums.ApiState;

public class CodeFailException extends ApiException {
   public CodeFailException(ApiState state, String message) {
       super(state, message);
   }
}
