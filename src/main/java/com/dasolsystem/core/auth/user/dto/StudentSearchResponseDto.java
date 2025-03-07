package com.dasolsystem.core.auth.user.dto;

import com.dasolsystem.core.entity.Deposit;
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
