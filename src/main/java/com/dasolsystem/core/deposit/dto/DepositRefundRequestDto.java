package com.dasolsystem.core.deposit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositRefundRequestDto {
    private String studentId;
    private String name;
}
