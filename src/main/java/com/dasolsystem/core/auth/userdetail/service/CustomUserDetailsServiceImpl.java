package com.dasolsystem.core.auth.userdetail.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.auth.userdetail.controller.CustomUserDetailsController;
import com.dasolsystem.core.auth.userdetail.dto.UserinfoDto;
import com.dasolsystem.core.entity.Member;
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
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        Member users = userRepository.findByStudentId(studentId).orElseThrow(
                () -> new UsernameNotFoundException(studentId)
        );
        UserinfoDto userinfoDto = UserinfoDto.builder()
                .studentId(users.getStudentId())
                .password(users.getPassword())
                .gender(users.getGender())
                .email(users.getEmail())
                .name(users.getName())
                .role(users.getRole())
                .build();
        return CustomUserDetailsController.builder()
                .userinfo(userinfoDto)
                .build();
    }

}
