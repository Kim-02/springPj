package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.handler.ResponseJson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //loaclhost:8080/login
@Slf4j
public class SignInController {

    private final signinService signinService;
    @PostMapping("/signin")
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSignincheckDto signincheckDto,
            HttpServletResponse res
            ){
        try{
            Map<String,String> headers = signinService.login(signincheckDto);
            res.setStatus(HttpServletResponse.SC_OK);
            res.setHeader("Content-Type",headers.get("Content-Type"));
            res.setHeader("Authorization",headers.get("Authorization"));
            res.setHeader("rAuthorization",headers.get("rAuthorization"));
            res.setHeader("User-Name",headers.get("User-Name"));
            res.setHeader("Message",headers.get("Message"));
        }catch(AuthFailException e){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("Message", e.getMessage());
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(res.getStatus())
                        .message(res.getHeader("Message"))
                        .result(res.getHeader("User-Name"))
                        .build()
        );
    }

}
