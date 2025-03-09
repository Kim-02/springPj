package com.dasolsystem.core.deposit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositUsersDto {
    private String name;
    private String studentId;
    private Integer amount;

}
