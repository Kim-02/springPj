package com.dasolsystem.core.auth.signup.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.entity.SignUp;
import com.dasolsystem.core.auth.signup.dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.enums.ApiState;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class signupService {

    private final authRepository signInRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Description("회원 가입")
    public ResponseSavedNameDto signup(RequestSignupPostDto request) {
        //TODO 어떻게 null검증을 할지 생각해서 id pw Null검증을 해야함.
        if(request.getEmail() == null || request.getEmail().trim().equals("")) {}
        if(signInRepo.existsByEmailID(request.getEmail())) {//중복검사
            throw new AuthFailException(ApiState.ERROR_701,"Exist User");
        }
        SignUp signUp = SignUp.builder()
                .emailID(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .userName(request.getUserName())
                .build();
        SignUp savedSignUp = signInRepo.save(signUp);
        return ResponseSavedNameDto.builder()
                .userName(savedSignUp.getUserName())
                .message("OK")
                .build();

    }
}
