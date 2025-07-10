package com.dasolsystem.core.approval.dto;

import com.dasolsystem.core.entity.ApprovalRequest;
import com.dasolsystem.core.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetApprovalPostResponse {
    private MemberDto approver;
    private List<ApprovalRequestsDto> approvalRequests;
}
