package com.dasolsystem.core.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponseDto {
    private String name;
    private String studentId;
    private String phone;
    private String gender;
    private boolean paidUser;
    private String email;
}
