package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.auth.signin.service.signinServiceImpl;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.signInJwtBuilderDto;
import com.dasolsystem.core.jwt.repository.JwtRepository;
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
    @PostMapping("/api/vo/signin")
    public ResponseEntity<ResponseJson<Object>> signin(
            @RequestBody RequestSignincheckDto signincheckDto,
            HttpServletResponse res
            ){
        String jwtToken = null;
        String refresh=null;
        ResponseSignincheckDto response = service.loginCheck(signincheckDto);
        if(response.getState()== ApiState.OK){
            jwtToken = jwtBuilder.generateAccessToken(response.getName());
            refresh = jwtBuilder.generateRefreshToken(response.getName());
            res.setHeader("Content-Type", "application/json");
            res.setHeader("Authorization", "Bearer " + jwtToken);
            res.setHeader("rAuthorization", "Bearer " + refresh);
            res.setHeader("User-Name",response.getName());
            jwtBuilder.saveRefreshToken(signInJwtBuilderDto.builder() //DB에 이름을 키로 한 리프레시 토큰 저장
                    .userName(response.getName())
                    .rtoken(refresh).build());
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
                        .result(res.getHeader("Authorization"))
                        .build()
        );
    }

}
