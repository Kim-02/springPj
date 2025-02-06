package com.dasolsystem.core.auth.user.controller;

import com.dasolsystem.core.auth.user.dto.UserinfoDto;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetailsController implements UserDetails {
    private UserinfoDto userinfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        //TODO 회원가입에서 역할을 넣는 부분 추가해야함.
        roles.add("ROLE_"+userinfo.getRole());

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userinfo.getPassword();
    }
    @Override
    public String getUsername() {
        return userinfo.getEmailId();
    }
}
