package com.dasolsystem.config;

import com.dasolsystem.handler.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExcptionHandler {

 //ToDo 나중에 DB에 에러 코드와 명칭을 담은 것을 만들어 불러와서 처리할 수 있도록 해야한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseJson<Object>> constraintViolationException(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> errors.put("Error.", error.getDefaultMessage()));
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(errors.get("Error."))
                        .status(401)
                        .build()
        );
    }
}
