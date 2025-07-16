package com.dasolsystem.core.trasaction.controller;

import com.dasolsystem.config.excption.FileException;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.trasaction.service.TransactionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionRecordController {
    private final TransactionRecordService transactionRecordService;

    @PostMapping("/record/append")
    public ResponseEntity<ResponseJson<?>> record(@RequestPart MultipartFile file) throws IOException {
        if(file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            throw new FileException(ApiState.ERROR_800,"파일 확장자나 파일 내부 구조가 손상되었습니다.");
        }
        ResponseJson<?> responseJson = transactionRecordService.appendRecordSave(file);
        return ResponseEntity.ok(responseJson);
    }
}
