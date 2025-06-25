package com.dasolsystem.core.auth.signup.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.auth.signup.dto.RequestSignupDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.signup.service.SignupService;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class SignUpController {

    private final SignupService signupService;

    @Description("회원가입 - 인증이 필요하지 않은 페이지")
    @PostMapping("/signup")
    public ResponseEntity<ResponseJson<Object>> signUp(@RequestBody @Valid RequestSignupDto requestSignupDto){
        try{
            ResponseSavedNameDto response = signupService.signup(requestSignupDto);

            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(response.getMessage())
                            .build()
            );
        }catch (AuthFailException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("Error.")
                            .result("Error."+e.getMessage())
                            .build()
            );
        }

    }

}