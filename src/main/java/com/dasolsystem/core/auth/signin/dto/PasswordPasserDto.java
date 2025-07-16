package com.dasolsystem.core.auth.signin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordPasserDto {
    private String oldPassword;
    private String newPassword;
}
