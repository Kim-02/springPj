package com.dasolsystem.core.approval.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalPostAcceptDto {
    private Long postId;
    private boolean approved;
}
