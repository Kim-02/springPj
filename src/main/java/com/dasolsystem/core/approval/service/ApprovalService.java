package com.dasolsystem.core.approval.service;

import com.dasolsystem.core.approval.dto.ApprovalPostAcceptDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestDto;
import com.dasolsystem.core.approval.dto.GetApprovalPostResponse;

public interface ApprovalService {
    Long postRequest(ApprovalRequestDto dto);
    Long approveRequestAccept(ApprovalPostAcceptDto dto, String studentId);
    GetApprovalPostResponse getApprovalPost(String studentId);
}
