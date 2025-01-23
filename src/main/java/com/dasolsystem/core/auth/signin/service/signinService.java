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
        System.out.println(id);
        System.out.println(pw);
        SignUp valid = repo.findByEmailID(id);
        System.out.println(valid);
        if(valid!=null){
            if(valid.getPassword().equals(pw)){
                return ResponseSignincheckDto.builder()
                        .state(State.OK)
                        .build();
            }
        }
        return ResponseSignincheckDto.builder()
                .state(State.NULL)
                .build();
    }

}
