package com.dasolsystem.core.expenditure.service;

import com.dasolsystem.core.expenditure.dto.ExpenditureRequestDto;
import com.dasolsystem.core.expenditure.dto.ExpenditureResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExpenditureService {
    List<ExpenditureResponseDto> saveExpendituresFromExcel(MultipartFile excelFile);
}
