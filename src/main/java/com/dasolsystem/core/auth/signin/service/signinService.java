package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.core.auth.Enum.State;
import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.auth.signin.Dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.Dto.ResponseSignincheckDto;
import com.dasolsystem.core.entity.SignUp;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class signinService {
    private final authRepository repo;

    @Description("로그인 check")
    public ResponseSignincheckDto loginCheck(RequestSignincheckDto dto) {
        String id = dto.getId();
        String pw = dto.getPw();
        SignUp valid = repo.findByEmailID(id);
        if(valid!=null){
            if(valid.getPassword().equals(pw)){
                return ResponseSignincheckDto.builder()
                        .state(State.OK)
                        .name(valid.getUserName())
                        .build();
            }
        }
        return ResponseSignincheckDto.builder()
                .state(State.NULL)
                .build();
    }

}
