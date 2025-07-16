package com.dasolsystem.core.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserEventParticipationResponseDto {
    private Long postId;
    private String postTitle;
    private String postContent;
    private String postTarget;
    private LocalDateTime postStartDate;
    private LocalDateTime postEndDate;
    private Integer eventPayAmount;
    private LocalDateTime eventPaidDate;
    private boolean eventPaidSuccess;
}
