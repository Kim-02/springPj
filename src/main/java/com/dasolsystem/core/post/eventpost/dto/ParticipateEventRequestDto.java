package com.dasolsystem.core.post.eventpost.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParticipateEventRequestDto {
    private Long postId;
    private List<Long> itemIds;
}
