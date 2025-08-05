package com.dasolsystem.core.post.eventpost.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventItemDto {
    private Long id;
    private String itemName;
    private String itemCost;
}
