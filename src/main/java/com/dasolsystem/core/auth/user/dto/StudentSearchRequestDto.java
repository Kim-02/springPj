package com.dasolsystem.core.auth.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSearchRequestDto {
    private String name;
    private String studentId;
}
