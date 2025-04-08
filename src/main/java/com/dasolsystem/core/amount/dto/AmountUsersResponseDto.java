package com.dasolsystem.core.amount.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmountUsersResponseDto {
    private String name;
    private String studentId;
    private Integer amount;
    private String paidUser;
    private String depositName;
}
