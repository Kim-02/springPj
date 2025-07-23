package com.dasolsystem.core.approval.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApprovalAllPostViewDto {
    private List<MemberDto> approvers;
    private ApprovalRequestsDto approvalRequests;
    private String byteFile;
}
