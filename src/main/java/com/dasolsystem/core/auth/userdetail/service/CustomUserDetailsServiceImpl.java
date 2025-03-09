package com.dasolsystem.core.auth.userdetail.service;

import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.auth.userdetail.controller.CustomUserDetailsController;
import com.dasolsystem.core.auth.userdetail.dto.UserinfoDto;
import com.dasolsystem.core.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        Users users = userRepository.findByEmailID(emailId);
        if(users == null) {
            throw new UsernameNotFoundException("Cannot find user with email id " + emailId);
        }
        UserinfoDto userinfoDto = UserinfoDto.builder()
                .emailId(users.getEmailID())
                .password(users.getPassword())
                .username(users.getName())
                .role(users.getRole())
                .build();
        return CustomUserDetailsController.builder()
                .userinfo(userinfoDto)
                .build();
    }

}
