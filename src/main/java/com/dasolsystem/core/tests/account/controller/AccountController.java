package com.dasolsystem.core.tests.account.controller;


import com.dasolsystem.config.excption.JsonFailException;
import com.dasolsystem.core.document.AccountES;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.tests.account.dto.MessageAccessDto;
import com.dasolsystem.core.tests.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/test")
    public ResponseEntity<ResponseJson<Object>> account(@RequestParam String message) {
        try{
            Long stime = System.nanoTime();
            ResponseJson<Object> JsonResult = accountService.findByMessage(message);
            Long etime = System.nanoTime();
            System.out.println("✅ Execution Time (ns): "+(etime-stime)+" ns");
            return ResponseEntity.ok(
                    JsonResult
            );
        }catch (JsonFailException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message(e.getMessage())
                            .build()
            );
        }

    }

    @GetMapping("/elktest")
    public ResponseEntity<ResponseJson<Object>> elktest(@RequestBody MessageAccessDto messageAccessDto) {
        System.out.println(messageAccessDto.getMessage());
        Long stime = System.nanoTime();
        List<AccountES> response = accountService.searchByMessage(messageAccessDto.getMessage());
        Long etime = System.nanoTime();
        System.out.println("✅ Execution Time (ns): "+(etime-stime)+" ns");
        System.out.println(response);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("ok")
                        .result(response)
                        .build()
        );
    }
}
