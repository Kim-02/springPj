package com.dasolsystem.core.auth.signup.service;

import com.dasolsystem.core.entity.SignUp;
import com.dasolsystem.core.auth.signup.Dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.Dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.repository.authRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class signupService {

    private final authRepository signInRepo;

    @Description("회원 가입")
    public ResponseSavedNameDto signup(RequestSignupPostDto request) {
        SignUp signUp = SignUp.builder()
                .emailID(request.getEmail())
                .password(request.getPassword())
                .userName(request.getUserName())
                .build();
        SignUp savedSignUp = signInRepo.save(signUp);
        return ResponseSavedNameDto.builder()
                .userName(savedSignUp.getUserName())
                .build();
    }
}
