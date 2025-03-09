package com.dasolsystem.core.deposit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositPersonalUpdateDto {
    private String studentId;
    private String depositType;
    private Integer amount;
}
