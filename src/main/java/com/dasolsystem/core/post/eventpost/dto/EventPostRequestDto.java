package com.dasolsystem.core.post.eventpost.dto;

import com.dasolsystem.core.entity.EventItem;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<EventItemDto> items;
}
