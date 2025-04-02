package com.dasolsystem.core.approval.service;

import com.dasolsystem.core.approval.dto.ApprovalPostDto;

import java.io.IOException;

public interface ApprovalService {
    Long saveApprovePost(ApprovalPostDto postDto) throws IOException;
}
