package com.dasolsystem.core.post.eventpost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventPostResponseDto {
    private Integer capacity;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String target;
    private String title;
    private Integer payAmount;
    private Boolean notice;
    private String memberName;
}
