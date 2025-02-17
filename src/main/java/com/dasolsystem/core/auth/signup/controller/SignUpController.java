package com.dasolsystem.core.auth.signup.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.auth.signup.dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.signup.service.signupService;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class SignUpController {

    private final signupService signupService;

    @Description("회원가입 - 인증이 필요하지 않은 페이지")
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseJson<Object>> signUp(@RequestBody @Valid RequestSignupPostDto requestSignupPostDto){
        try{
            ResponseSavedNameDto response = signupService.signup(requestSignupPostDto);

            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message(response.getMessage())
                            .result(response)
                            .build()
            );
        }catch (AuthFailException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(701)
                            .message("Error."+e.getMessage())
                            .result("Error.")
                            .build()
            );
        }

    }

}