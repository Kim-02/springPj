package com.dasolsystem.core.deposit.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositUsersResponseDto<T> {
    private T result;
}
