package com.dasolsystem.core.auth.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSearchResponseDto {
    private String studentId;
    private String name;
    private List<Deposit> deposits;
}
