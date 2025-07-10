package com.dasolsystem.core.approval.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;
    private String studentId;
    private String name;
}
