package com.dasolsystem.core.deposit.controller;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.deposit.dto.*;
import com.dasolsystem.core.deposit.service.DepositService;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/amount")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/update")
    public ResponseEntity<ResponseJson<Object>> update(@ModelAttribute DepositUsersRequestDto requestDto) throws IOException {
        try{
            DepositUsersResponseDto<?> res = depositService.updateDeposit(requestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("ok")
                            .result(res.getResult())
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(502)
                            .result(e.getMessage())
                            .message("error")
                            .build()
            );
        }


    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadDepositExcel(@RequestParam("amountType") String depositType) throws IOException {
        // 데이터 조회
        List<DepositUsersDto> depositUsers = depositService.findDepositUsers(depositType);

        // Excel 파일 생성
        ByteArrayOutputStream excelStream = depositService.generateExcelFile(depositUsers);

        // 응답 헤더 설정 (파일 다운로드)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users_deposit.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelStream.toByteArray());
    }

    @PostMapping("/personal/update")
    public ResponseEntity<ResponseJson<Object>> updatePersonal(@RequestBody DepositPersonalUpdateDto depositPersonalUpdateDto) {
        try{
            String result = depositService.updatePersonalDeposit(depositPersonalUpdateDto.getStudentId(),depositPersonalUpdateDto.getDepositType(),depositPersonalUpdateDto.getAmount());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(result)
                    .build()
            );
        }catch (DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<ResponseJson<Object>> depositRefund(@RequestBody DepositRefundRequestDto requestDto){
        try{
            String result = depositService.depositRefund(requestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(result)
                            .build()
            );
        }catch (DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }
    }
}
