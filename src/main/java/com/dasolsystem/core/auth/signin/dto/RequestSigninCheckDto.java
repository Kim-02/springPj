package com.dasolsystem.core.auth.signin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSigninCheckDto {
    private String studentId;
    private String password;
}
