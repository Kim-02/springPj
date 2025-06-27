package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.signin.dto.RequestSigninCheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.handler.ResponseJson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") //loaclhost:8080/login
@Slf4j
public class SignInController {

    private final signinService signinService;
    @PostMapping("/login")
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSigninCheckDto signinCheckDto,
            HttpServletResponse res
            ){
        String name = "";
        try{
            Map<String,String> headers = signinService.login(signinCheckDto);
            res.setStatus(HttpServletResponse.SC_OK);
            res.setHeader("Content-Type",headers.get("Content-Type"));
            res.setHeader("Authorization",headers.get("Authorization"));
            res.setHeader("rAuthorization",headers.get("rAuthorization"));
            name = headers.get("name");
        }catch(AuthFailException | UsernameNotFoundException | BadCredentialsException e){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("Error.", e.getMessage());
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(res.getStatus())
                        .message("Success")
                        .result("User name: "+name)
                        .build()
        );
    }

}
