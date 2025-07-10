package com.dasolsystem.core.approval.dto;

import com.dasolsystem.core.entity.ApprovalCode;
import com.dasolsystem.core.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApprovalRequestDto {
    private String accountNumber;
    private String receiptFile; //파일 경로
    private LocalDateTime requestDate;
    private String requestDetails;
    private Integer requestAmount;
    private String title;
    private String approvalCode;
    private String studentId; //요청자 학번
    private String payerName;
    private List<String> approversId; //승인자 아이디
}
