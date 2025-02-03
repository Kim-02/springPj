package com.dasolsystem.core.auth.signin.service;

import com.dasolsystem.core.auth.signin.dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.dto.ResponseSignincheckDto;
import org.springframework.stereotype.Service;


public interface signinService {
    ResponseSignincheckDto loginCheck(RequestSignincheckDto dto);
}
