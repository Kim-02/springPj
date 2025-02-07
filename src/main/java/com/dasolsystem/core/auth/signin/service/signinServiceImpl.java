package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.entity.SignUp;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class signinServiceImpl implements signinService {
    private final authRepository repo;
    private final JwtBuilder jwtBuilder;
    private final PasswordEncoder passwordEncoder;


    @Description("로그인")
    public Map<String, String> login(RequestSignincheckDto dto)  {
        String jwtToken;
        Long refreshTokenId;
        ResponseSignincheckDto responseDto = loginCheck(dto);
        Map<String,String> headerMap = new HashMap<>();
        if(responseDto.getState()== ApiState.OK){
            jwtToken = jwtBuilder.generateAccessToken(responseDto.getEmailId());
            refreshTokenId = jwtBuilder.getRefreshTokenId(responseDto.getEmailId());
            headerMap.put("Content-Type", "application/json");
            headerMap.put("Authorization", "Bearer " + jwtToken);
            headerMap.put("rAuthorization", "Bearer " + refreshTokenId);
            headerMap.put("User-Name",responseDto.getName());
            headerMap.put("Message",responseDto.getMessage());
            log.info("✅ Api state Ok");
            log.info("Username: "+headerMap.get("User-Name"));
        }
        else if(responseDto.getState()==ApiState.ERROR_901){
            throw new AuthFailException(ApiState.ERROR_901,responseDto.getMessage());
        }
        else{
            throw new AuthFailException(ApiState.ERROR_UNKNOWN,"Unknown Error");
        }
        return headerMap;
    }

    @Description("로그인 check")
    public ResponseSignincheckDto loginCheck(RequestSignincheckDto dto) {
        String id = dto.getId();
        String pw = dto.getPw();
        SignUp valid = repo.findByEmailID(id);
        if(valid == null){
            throw new UsernameNotFoundException("Not Found id");
        }
        if(!passwordEncoder.matches(pw, valid.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }
        return ResponseSignincheckDto.builder()
                .state(ApiState.OK)
                .emailId(valid.getEmailID())
                .message("login success")
                .name(valid.getUserName())
                .build();



    }

}
