package com.dasolsystem.core.deposit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositResultDto {
    private String noneFinds;
    private String duplicated;
}
