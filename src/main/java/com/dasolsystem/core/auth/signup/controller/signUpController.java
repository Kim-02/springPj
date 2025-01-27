package com.dasolsystem.core.auth.signup.controller;

import com.dasolsystem.config.MvcConfiguration;
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
@RequestMapping("/signup")
public class signUpController extends MvcConfiguration {

    private final signupService service;

    @Description("회원가입")
    @PostMapping("/api/sign-up")
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
    @Description("회원가입 페이지로 이동")
    @GetMapping("/sign-up-page")
    public String signUpPage(){
        return "sign-up-page";
    }


}
