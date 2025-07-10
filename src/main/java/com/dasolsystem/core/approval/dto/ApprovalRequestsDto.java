package com.dasolsystem.core.approval.dto;

import com.dasolsystem.core.entity.ApprovalCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalRequestsDto {
    private Long requestId;
    private String memberName;
    private LocalDateTime requestDate;
    private String title;
    private Integer requestedAmount;
    private String accountNumber;
    private String payerName;
    private String requestDetail;
    private String receiptFile;
    private String approvalCode;
    private Boolean isCompleted;
    private LocalDateTime approvalDate;
}
