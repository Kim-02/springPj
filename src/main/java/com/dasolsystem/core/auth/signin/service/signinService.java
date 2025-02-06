package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;

import java.util.Map;


public interface signinService {
    ResponseSignincheckDto loginCheck(RequestSignincheckDto dto);
    Map<String,String> login(RequestSignincheckDto dto);
}
