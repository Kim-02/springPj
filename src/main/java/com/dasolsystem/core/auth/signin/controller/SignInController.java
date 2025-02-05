package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //loaclhost:8080/login
@Slf4j
public class SignInController {
    private final signinService service;
    private final JwtBuilder jwtBuilder;
    @PostMapping("/signin")
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSignincheckDto signincheckDto,
            HttpServletResponse res
            ){
        String jwtToken = null;
        Long refreshTokenId =null;
        ResponseSignincheckDto response = service.loginCheck(signincheckDto);
        if(response.getState()== ApiState.OK){
            jwtToken = jwtBuilder.generateAccessToken(response.getName());
            refreshTokenId = jwtBuilder.getRefreshTokenId(response.getName());
            res.setHeader("Content-Type", "application/json");
            res.setHeader("Authorization", "Bearer " + jwtToken);
            res.setHeader("rAuthorization", "Bearer " + refreshTokenId);
            res.setHeader("User-Name",response.getName());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            response.getName(),
                            jwtToken.replace("Bearer ", ""),
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            jwtBuilder.getAccessTokenPayload(jwtToken.replace("Bearer ", "")))));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("✅ 인증 객체 등록 확인: {}", auth);
            log.info("▶ 사용자 이름: {}", auth.getName());
            log.info("▶ 권한 목록: {}", auth.getAuthorities());
            log.info("▶ 인증 여부: {}", auth.isAuthenticated());

        }
        else if(response.getState()==ApiState.ERROR_901){
            res.setHeader("Authorization", response.getMessage() );
        }
        else{
            res.setHeader("Authorization","UNKNOWN ERROR" );
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("JWT")
                        .result(res.getHeader("User-Name"))
                        .build()
        );
    }

}
