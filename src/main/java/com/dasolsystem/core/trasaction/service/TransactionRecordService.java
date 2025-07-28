package com.dasolsystem.core.trasaction.service;

import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.trasaction.dto.ExpendTransactionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TransactionRecordService {
    ResponseJson<?> appendRecordSave(MultipartFile file) throws IOException;
    void expendRecordSave(ExpendTransactionDto dto);
    ResponseJson<?> getTotalAmount();
}
