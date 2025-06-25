package com.dasolsystem.core.auth.signin.dto;

import com.dasolsystem.core.entity.Role;
import com.dasolsystem.core.enums.ApiState;
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
    private String studentId;
    private String name;
    private String message;
    private String roleCode;
}
