package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.core.auth.signin.dto.RequestSigninCheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;

import java.util.Map;


public interface signinService {
    ResponseSignincheckDto loginCheck(RequestSigninCheckDto dto);
    Map<String, String> login(RequestSigninCheckDto dto);
    void checkPassword(String studentId, String password);
    void changePassword(String studentId, String newPassword);
}
