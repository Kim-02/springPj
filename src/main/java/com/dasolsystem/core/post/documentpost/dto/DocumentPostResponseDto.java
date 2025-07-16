package com.dasolsystem.core.post.documentpost.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentPostResponseDto {
    private Long id;
    private Integer capacity;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String target;
    private String title;
    private String filePath;
    private String realLocation;
    private String memberName;
}
