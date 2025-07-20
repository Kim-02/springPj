package com.dasolsystem.core.file;

import com.dasolsystem.core.file.dto.FileUploadDto;

import java.io.IOException;

public interface FileControlService {
    String uploadFile(FileUploadDto fileUploadDto) throws IOException;
    byte[] getFileBytes(String key) throws IOException;
}
