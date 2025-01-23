package com.dasolsystem.core.auth.signup.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSignupPostDto {
    private String email;
    private String password;
    private String userName;
}
