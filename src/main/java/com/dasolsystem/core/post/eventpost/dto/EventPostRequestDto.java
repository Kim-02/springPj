package com.dasolsystem.core.post.eventpost.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventPostRequestDto {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String target;
    private Integer capacity;
    private Boolean notice;
    private Integer payAmount;
    private String studentId;
}
