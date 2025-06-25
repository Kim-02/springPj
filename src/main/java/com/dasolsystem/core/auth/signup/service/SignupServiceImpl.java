package com.dasolsystem.core.auth.signup.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.signup.dto.RequestSignupDto;
import com.dasolsystem.core.auth.signup.dto.ResponseSavedNameDto;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.Role;
import com.dasolsystem.core.enums.ApiState;
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
    private final RoleRepository roleRepository;

    @Description("회원 가입")
    @Transactional
    public ResponseSavedNameDto signup(RequestSignupDto request) {
        if(userRepository.existsBystudentId(request.getStudent_id()))
            throw new AuthFailException(ApiState.ERROR_701,"Exist User");
        Role default_role = roleRepository.findById(100L);
        Member user = Member.builder()
                .studentId(request.getStudent_id())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .paidUser(false)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .role(default_role)
                .build();

        Member savedUsers = userRepository.save(user);
        return ResponseSavedNameDto.builder()
                .userName(savedUsers.getName())
                .message("Signup Success")
                .build();
    }
}
