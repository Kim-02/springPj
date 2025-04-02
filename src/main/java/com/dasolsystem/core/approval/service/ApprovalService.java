package com.dasolsystem.core.approval.service;

import com.dasolsystem.core.approval.dto.ApprovalPostDto;
import com.dasolsystem.core.approval.dto.ApprovalSummaryDto;

import java.io.IOException;
import java.util.List;

public interface ApprovalService {
    Long saveApprovePost(ApprovalPostDto postDto) throws IOException;
    List<ApprovalSummaryDto> getApprovalSummaries();
}
