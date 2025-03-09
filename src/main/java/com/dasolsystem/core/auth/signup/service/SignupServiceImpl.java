package com.dasolsystem.core.auth.signup.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.signup.dto.RequestSignupDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.enums.Role;
import jakarta.transaction.Transactional;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Description("회원 가입 : 한명씩 등록하는 용도")
    @Transactional
    public ResponseSavedNameDto signup(RequestSignupDto request) {
        if(userRepository.existsByEmailID(request.getEmail())
                || userRepository.existsBystudentId(request.getStudentId())) {//중복검사
            throw new AuthFailException(ApiState.ERROR_701,"Exist User");
        }
        Users user = Users.builder()
                .studentId(request.getStudentId())
                .name(request.getName())
                .emailID(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .role(Role.User)
                .build();
        Users savedUsers = userRepository.save(user);
        return ResponseSavedNameDto.builder()
                .userName(savedUsers.getName())
                .message("Signup Success")
                .build();

    }
}
