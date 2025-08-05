package com.dasolsystem.core.post.eventpost.dto;

import com.dasolsystem.core.entity.EventItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<EventItemDto> eventItem;
}
