package com.dasolsystem.core.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="trasaction_record")
public class TransactionRecord {
    /** 거래 내역 고유 ID (PK, AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_id", updatable = false, nullable = false)
    private Integer txId;

    /** 입금·출금한 회원 (FK → 회원.user_id) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** 연관된 결재 요청 (출금인 경우만 채워짐; 입금일 땐 NULL) */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "request_id", nullable = true)
    private ApprovalRequest approvalRequest;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "tx_date", nullable = false)
    private LocalDateTime txDate;

    /** TRUE: 출금, FALSE: 입금 (default → false) */
    @Column(name = "is_expense", nullable = false)
    private Boolean expense = false;
}
