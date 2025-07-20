package com.dasolsystem.core.file.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class FileUploadDto {
    MultipartFile file;
    String path;
}
