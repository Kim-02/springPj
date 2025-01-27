package com.dasolsystem.core.auth.signup.controller;

import com.dasolsystem.core.post.Dto.ResponseJson;
import com.dasolsystem.core.auth.signup.dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.signup.service.signupService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class signUpController {

    private final signupService service;

    @Description("회원가입")
    @PostMapping
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
