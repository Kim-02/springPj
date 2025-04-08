package com.dasolsystem.core.amount.service;

import com.dasolsystem.core.amount.dto.AmountUsersResponseDto;
import com.dasolsystem.core.file.dto.StudentIdDto;

import java.util.List;

public interface AmountService {
    List<AmountUsersResponseDto> checkFeeStatus(List<StudentIdDto> studentIdsDtoList, List<String> depositNames);
}
