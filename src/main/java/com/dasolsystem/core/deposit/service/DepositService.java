package com.dasolsystem.core.deposit.service;

import com.dasolsystem.core.deposit.dto.DepositUsersDto;
import com.dasolsystem.core.deposit.dto.DepositUsersRequestDto;
import com.dasolsystem.core.deposit.dto.DepositUsersResponseDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface DepositService {
    DepositUsersResponseDto<Object> updateDeposit(DepositUsersRequestDto requestDto) throws IOException;
    ByteArrayOutputStream generateExcelFile(List<DepositUsersDto> depositUsers) throws IOException;
    List<DepositUsersDto> findDepositUsers(String depositType);
    String updatePersonalDeposit(String studentId, String depositType, Integer amount );
}
