package com.dasolsystem.core.deposit.service;

import com.dasolsystem.core.deposit.dto.DepositUsersRequestDto;
import com.dasolsystem.core.deposit.dto.DepositUsersResponseDto;

import java.io.IOException;

public interface DepositService {
    DepositUsersResponseDto updateDeposit(DepositUsersRequestDto requestDto) throws IOException;
}
