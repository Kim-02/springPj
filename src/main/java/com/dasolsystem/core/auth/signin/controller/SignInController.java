package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.signin.dto.PasswordPasserDto;
import com.dasolsystem.core.auth.signin.dto.RequestSigninCheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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
    private final SecurityGuardian securityGuardian;
    @PostMapping("/login")
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSigninCheckDto signinCheckDto,
            HttpServletResponse res
            ){
        String name = "";
            Map<String,String> headers = signinService.login(signinCheckDto);
            res.setStatus(HttpServletResponse.SC_OK);
            res.setHeader("Content-Type",headers.get("Content-Type"));
            res.setHeader("Authorization",headers.get("Authorization"));
            res.setHeader("rAuthorization",headers.get("rAuthorization"));
            name = headers.get("name");
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(res.getStatus())
                        .message("Success")
                        .result("User name: "+name)
                        .build()
        );
    }

    @PostMapping("/password/check")
    public ResponseEntity<ResponseJson<Object>> checkPassword(@RequestBody PasswordPasserDto oldPassword, HttpServletRequest request) {
        Claims loginClaims = securityGuardian.getServletTokenClaims(request);
        signinService.checkPassword(loginClaims.getSubject(),oldPassword.getOldPassword());
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success")
                        .build()
        );
    }
    @PostMapping("/password/change")
    public ResponseEntity<ResponseJson<Object>> changePassword(
            @RequestBody PasswordPasserDto newPassword,
            HttpServletRequest request
    ){
        Claims loginClaims = securityGuardian.getServletTokenClaims(request);
        signinService.changePassword(loginClaims.getSubject(),newPassword.getNewPassword());
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success change")
                        .build()
        );
    }
}
