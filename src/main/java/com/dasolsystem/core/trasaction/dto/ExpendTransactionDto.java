package com.dasolsystem.core.trasaction.dto;

import com.dasolsystem.core.entity.ApprovalRequest;
import com.dasolsystem.core.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpendTransactionDto {
    private Member member;
    private ApprovalRequest approvalRequest;
    private Integer amount;
}
