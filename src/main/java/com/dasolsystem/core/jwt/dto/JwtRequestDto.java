package com.dasolsystem.core.jwt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtRequestDto {
    private String studentId;
    private String name;
    private String role;
}
