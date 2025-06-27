package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.signin.dto.RequestSigninCheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.jwt.dto.JwtRequestDto;
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
    private final UserRepository userRepository;
    private final JwtBuilder jwtBuilder;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Description("로그인")
    public Map<String, String> login(RequestSigninCheckDto dto)  {
        String jwtToken;
        Long refreshTokenId;
        ResponseSignincheckDto responseDto = loginCheck(dto);
        Map<String,String> headerMap = new HashMap<>();
        if(responseDto.getState()== ApiState.OK){
            jwtToken = jwtBuilder.generateAccessJWT(
                    JwtRequestDto.builder()
                            .role(
                                    roleRepository.findByCode(responseDto.getRoleCode())
                                            .orElseThrow(() -> new DBFaillException(ApiState.ERROR_501))
                                            .getName()
                            )
                            .studentId(responseDto.getStudentId())
                            .name(responseDto.getName())
                            .build()
            );
            refreshTokenId = jwtBuilder.generateRefreshId(responseDto.getStudentId());
            headerMap.put("Content-Type", "application/json");
            headerMap.put("Authorization", "Bearer " + jwtToken);
            headerMap.put("rAuthorization", "Bearer " + refreshTokenId);
            headerMap.put("name", responseDto.getName());
            log.info("✅ Login Success");
            log.info("login Id "+responseDto.getStudentId());
            log.info("login Name "+responseDto.getName());
            log.info("login role "+responseDto.getRoleCode());
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
    public ResponseSignincheckDto loginCheck(RequestSigninCheckDto dto) {
        String id = dto.getStudent_id();
        String pw = dto.getPw();
        Member valid = userRepository.findByStudentIdWithRole(id).orElseThrow(()-> new UsernameNotFoundException("Not Found id"));
        if(!passwordEncoder.matches(pw, valid.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }
        return ResponseSignincheckDto.builder()
                .state(ApiState.OK)
                .studentId(valid.getStudentId())
                .roleCode(valid.getRole().getCode())
                .message("login success")
                .name(valid.getName())
                .build();
    }

}
