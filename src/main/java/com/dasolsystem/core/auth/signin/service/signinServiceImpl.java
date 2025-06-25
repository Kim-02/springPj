package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
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
    private final UserRepository repo;
    private final JwtBuilder jwtBuilder;
    private final PasswordEncoder passwordEncoder;


    @Description("로그인")
    public Map<String, String> login(RequestSignincheckDto dto)  {
        String jwtToken;
        Long refreshTokenId;
        ResponseSignincheckDto responseDto = loginCheck(dto);
        Map<String,String> headerMap = new HashMap<>();
        if(responseDto.getState()== ApiState.OK){
            jwtToken = jwtBuilder.generateAccessToken(responseDto.getEmailId(),responseDto.getRole(), responseDto.getName());
            refreshTokenId = jwtBuilder.getRefreshTokenId(responseDto.getEmailId());
            headerMap.put("Content-Type", "application/json");
            headerMap.put("Authorization", "Bearer " + jwtToken);
            headerMap.put("rAuthorization", "Bearer " + refreshTokenId);
            headerMap.put("User-Name",responseDto.getName());
            headerMap.put("Message",responseDto.getMessage());
            log.info("✅ Login Success");
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
        Member valid = repo.findByEmailID(id);
        if(valid == null){
            throw new UsernameNotFoundException("Not Found id");
        }
        if(!passwordEncoder.matches(pw, valid.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }
        return ResponseSignincheckDto.builder()
                .state(ApiState.OK)
                .emailId(valid.getEmailID())
                .role(valid.getRole())
                .message("login success")
                .name(valid.getName())
                .build();



    }

}
