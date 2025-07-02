package com.dasolsystem.core.post.inquirypost.dto;

import com.dasolsystem.core.entity.CodeInquiry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryPostResponseDto {
    private Integer capacity;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String target;
    private String title;
    private CodeInquiry inquiryCode;
    private String memberName;
}
