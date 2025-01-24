package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.core.auth.signin.Dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.Dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class signinController {
    private final signinService service;
    private final JwtBuilder jwtBuilder;
    @PostMapping
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSignincheckDto signincheckDto
            ){
        String jwtToken = null;
        ResponseSignincheckDto response = service.loginCheck(signincheckDto);
        if(response.getState().value){
            jwtToken = jwtBuilder.generateAccessToken(response.getName());
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("JWT")
                        .result(jwtToken)
                        .build()
        );
    }



}
