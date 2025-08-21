package com.dasolsystem.core.auth.signup.service;


import com.dasolsystem.core.auth.signup.dto.RequestSignupDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;

public interface SignupService {
    ResponseSavedNameDto signup(RequestSignupDto request);
    void emailVerificationCode(String email);
}
