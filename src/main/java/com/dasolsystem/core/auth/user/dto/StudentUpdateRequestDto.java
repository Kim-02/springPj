package com.dasolsystem.core.auth.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StudentUpdateRequestDto {
    private Role role;
    private String studentId;

}
