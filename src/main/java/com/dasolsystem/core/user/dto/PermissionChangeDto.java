package com.dasolsystem.core.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionChangeDto {
    private String reason;
    private String targetStudentId;
    private String role;
}
