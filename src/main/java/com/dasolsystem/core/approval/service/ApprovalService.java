package com.dasolsystem.core.approval.service;

import com.dasolsystem.core.approval.dto.ApprovalAllPostViewDto;
import com.dasolsystem.core.approval.dto.ApprovalPostAcceptDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestDto;
import com.dasolsystem.core.approval.dto.GetApprovalPostResponse;

import java.io.IOException;
import java.util.List;

public interface ApprovalService {
    Long postRequest(ApprovalRequestDto dto) throws IOException;
    Long approveRequestAccept(ApprovalPostAcceptDto dto, String studentId);
    GetApprovalPostResponse getApprovalPost(String studentId) throws IOException;
    List<ApprovalAllPostViewDto> getAllApprovalRequests() throws IOException;
}
