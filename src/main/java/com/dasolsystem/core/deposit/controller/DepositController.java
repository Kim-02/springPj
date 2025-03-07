package com.dasolsystem.core.deposit.controller;

import com.dasolsystem.core.deposit.dto.DepositUsersRequestDto;
import com.dasolsystem.core.deposit.dto.DepositUsersResponseDto;
import com.dasolsystem.core.deposit.service.DepositService;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/amount")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/update")
    public ResponseEntity<ResponseJson<Object>> update(@ModelAttribute DepositUsersRequestDto requestDto) throws IOException {
        try{
            DepositUsersResponseDto res = depositService.updateDeposit(requestDto);
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
}
