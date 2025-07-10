package com.dasolsystem.config;

import com.dasolsystem.config.excption.ApiException;
import com.dasolsystem.core.handler.ResponseJson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseJson<?>> handelAuthFailException(ApiException e) {
        ResponseJson<?> body = ResponseJson.builder()
                .status(e.getCode())
                .message(e.getMessage())
                .build();
        return ResponseEntity
                .status(e.getCode())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseJson<?>> handleUnknownException(Exception e) {
        // 알 수 없는 예외
        ResponseJson<?> body = ResponseJson.builder()
                .status(100)
                .message("예기치 못한 오류가 발생했습니다: " + e.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
