package com.dasolsystem.core.approval.service;

import com.dasolsystem.core.approval.dto.*;

import java.io.IOException;
import java.util.List;

public interface ApprovalService {
    Long postRequest(ApprovalRequestDto dto) throws IOException;
    Long approveRequestAccept(ApprovalPostAcceptDto dto, String studentId);
    GetApprovalPostResponse getApprovalPost(String studentId) throws IOException;
    List<ApprovalAllPostViewDto> getAllApprovalRequests() throws IOException;

    /** 월별 목록 (이미지 제외, 전체 리스트) */
    List<ApprovalRequestsDto> getMonthlyApprovals(String month);

    /** 단건 상세 (이미지 포함 + 승인자 목록 포함) */
    ApprovalAllPostViewDto getApprovalDetail(Long requestId) throws IOException;
}
