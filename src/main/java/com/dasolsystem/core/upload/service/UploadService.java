package com.dasolsystem.core.upload.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    void processExcelFile(MultipartFile memberFile);
}
