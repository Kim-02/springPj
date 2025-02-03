package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import com.dasolsystem.core.entity.SignUp;
import com.dasolsystem.core.enums.ApiState;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class signinServiceImpl implements signinService {
    private final authRepository repo;

    @Description("로그인 check")
    public ResponseSignincheckDto loginCheck(RequestSignincheckDto dto) {
        String id = dto.getId();
        String pw = dto.getPw();
        SignUp valid = repo.findByEmailID(id);
        if(valid!=null){
            if(valid.getPassword().equals(pw)){
                return ResponseSignincheckDto.builder()
                        .state(ApiState.OK)
                        .name(valid.getUserName())
                        .build();
            }
        }
        return ResponseSignincheckDto.builder()
                .state(ApiState.ERROR_901)
                .message("login failed")
                .build();
    }

}
