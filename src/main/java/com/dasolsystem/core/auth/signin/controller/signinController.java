package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login") //loaclhost:8080/login
public class signinController {
    private final signinService service;
    private final JwtBuilder jwtBuilder;
    @PostMapping
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSignincheckDto signincheckDto,
            HttpServletResponse res
            ){
        String jwtToken = null;
        ResponseSignincheckDto response = service.loginCheck(signincheckDto);
        if(response.getState().value){
            jwtToken = jwtBuilder.generateAccessToken(response.getName());
            res.setHeader("Content-Type", "application/json");
            res.setHeader("Authorization", "Bearer " + jwtToken);
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("JWT")
                        .result(res.getHeader("Authorization"))
                        .build()
        );
    }

}
