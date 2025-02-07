package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.auth.user.controller.CustomUserDetailsController;
import com.dasolsystem.core.auth.user.dto.UserinfoDto;
import com.dasolsystem.core.entity.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final authRepository authRepository;
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        SignUp users = authRepository.findByEmailID(emailId);
        if(users == null) {
            throw new UsernameNotFoundException("Cannot find user with email id " + emailId);
        }
        UserinfoDto userinfoDto = UserinfoDto.builder()
                .emailId(users.getEmailID())
                .password(users.getPassword())
                .username(users.getUserName())
                .role(users.getRole())
                .build();
        return CustomUserDetailsController.builder()
                .userinfo(userinfoDto)
                .build();
    }

}
