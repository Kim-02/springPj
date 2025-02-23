package com.dasolsystem.core.auth.signin.dto;

import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSignincheckDto {
    private ApiState state;
    private String emailId;
    private String name;
    private String message;
    private Role role;
}
