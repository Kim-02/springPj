package com.dasolsystem.core.auth.signup.controller;

import com.dasolsystem.core.post.Dto.ResponseJson;
import com.dasolsystem.core.auth.signup.dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.signup.service.signupService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignUpController {

    private final signupService service;

    @Description("회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseJson<Object>> signUp(@RequestBody RequestSignupPostDto requestSignupPostDto){

        ResponseSavedNameDto response = service.signup(requestSignupPostDto);

        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(response)
                        .build()
        );
    }

}
