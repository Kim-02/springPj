package com.dasolsystem.core.amount.controller;

import com.dasolsystem.core.amount.dto.AmountCheckRequestDto;
import com.dasolsystem.core.amount.dto.AmountUsersResponseDto;
import com.dasolsystem.core.amount.service.AmountService;
import com.dasolsystem.core.deposit.dto.DepositUsersDto;
import com.dasolsystem.core.deposit.service.DepositService;
import com.dasolsystem.core.file.dto.StudentIdDto;
import com.dasolsystem.core.file.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/check_amount")
public class AmountController {
    private final ExcelService excelService;
    private final AmountService amountService;
    //학번으로 입금 여부를 찾는 로직
    @PostMapping("/findexpender")
    public ResponseEntity<byte[]> checkStudentFeeStatus(@ModelAttribute AmountCheckRequestDto requestDto) throws IOException {
        // 엑셀 파일에서 학번을 DTO로 추출
        List<StudentIdDto> studentIdsDtoList = excelService.extractStudentIdsFromExcel(requestDto.getFile());

        // DB에서 사용자 정보와 입금내역을 확인
        List<AmountUsersResponseDto> results = amountService.checkFeeStatus(studentIdsDtoList, requestDto.getDepositNames());

        // 결과를 엑셀로 생성
        ByteArrayOutputStream excelOutput = excelService.generateResultExcel(results);

        // 응답 헤더 설정 (파일 다운로드)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "fee_status_result.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelOutput.toByteArray());
    }
}
