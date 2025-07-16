package com.dasolsystem.core.post.documentpost.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentPostRequestDto {

    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String target;
    private Integer capacity;
    private String filePath;
    private String realLocation;
    private String studentId;
}
