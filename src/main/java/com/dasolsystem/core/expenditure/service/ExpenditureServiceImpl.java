package com.dasolsystem.core.expenditure.service;

import com.dasolsystem.core.entity.Expenditure;
import com.dasolsystem.core.expenditure.dto.ExpenditureRequestDto;
import com.dasolsystem.core.expenditure.dto.ExpenditureResponseDto;
import com.dasolsystem.core.expenditure.repository.ExpenditureRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenditureServiceImpl implements ExpenditureService {
    private final ExpenditureRepository expenditureRepository;

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Transactional
    public List<ExpenditureResponseDto> saveExpendituresFromExcel(MultipartFile excelFile) {
        List<ExpenditureResponseDto> responses = new ArrayList<>();

        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            // 첫 번째 행은 헤더라고 가정하고 1번 행부터 시작
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 예시: 0번 셀: 거래 일시, 1번 셀: 출금액, 2번 셀: 내용
                // 여기서는 dateCell: index 2, withdrawalCell: index 5, contentCell: index 6
                Cell dateCell = row.getCell(2);
                Cell withdrawalCell = row.getCell(5);
                Cell contentCell = row.getCell(6);

                if (dateCell == null || withdrawalCell == null || contentCell == null) {
                    continue; // 데이터가 불완전한 경우 건너뜁니다.
                }

                // 거래 일시는 문자열로 가정 (예: "20250313201039")
                String transactionDateStr = dateCell.getStringCellValue().trim();

                // 출금액은 셀 타입에 따라 처리 (NUMERIC 또는 STRING)
                int withdrawalAmount;
                if (withdrawalCell.getCellType() == CellType.NUMERIC) {
                    withdrawalAmount = (int) withdrawalCell.getNumericCellValue();
                } else {
                    String withdrawalAmountStr = withdrawalCell.getStringCellValue().trim();
                    withdrawalAmount = Integer.parseInt(withdrawalAmountStr);
                }
                // 출금액이 0이면 건너뜁니다.
                if (withdrawalAmount == 0) {
                    continue;
                }

                String content = contentCell.getStringCellValue().trim();

                // 거래 일시 문자열을 파싱하여 LocalDate로 변환 (예: 2025-03-13)
                LocalDateTime dateTime = LocalDateTime.parse(transactionDateStr, inputFormatter);
                LocalDate transactionDate = dateTime.toLocalDate();

                // 중복 체크: 동일한 거래 일시, 출금액, 내용이 이미 존재하는지 확인
                Optional<Expenditure> existing = expenditureRepository
                        .findByTransactionDateAndWithdrawalAmountAndContent(transactionDate, withdrawalAmount, content);

                // 중복이 없을 경우에만 저장 처리
                if (!existing.isPresent()) {
                    Expenditure expenditure = Expenditure.builder()
                            .transactionDate(transactionDate)
                            .withdrawalAmount(withdrawalAmount)
                            .content(content)
                            .build();

                    expenditureRepository.save(expenditure);

                    ExpenditureResponseDto responseDto = ExpenditureResponseDto.builder()
                            .expenditureDate(expenditure.getTransactionDate())
                            .expenditureAmount(expenditure.getWithdrawalAmount())
                            .expenditureContent(expenditure.getContent())
                            .build();

                    responses.add(responseDto);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Excel 파일 처리 중 오류가 발생했습니다.", e);
        }

        return responses;
    }

}
