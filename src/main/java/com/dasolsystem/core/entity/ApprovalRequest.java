package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="approval_request")
public class ApprovalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", updatable = false, nullable = false)
    private Integer requestId;

    /** 요청자 (기안자 회원) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** 요청 생성 일시 */
    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    /** 제목 */
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    /** 기안자 이름 */
    @Column(name = "drafter_name", length = 100, nullable = false)
    private String drafterName;

    /** 결재 요청 금액 (원 단위) */
    @Column(name = "requested_amount", nullable = false)
    private Integer requestedAmount;

    /** 결재자 이름 */
    @Column(name = "approver_name", length = 100, nullable = false)
    private String approverName;

    /** 계좌번호 */
    @Column(name = "account_number", length = 50, nullable = false)
    private String accountNumber;

    /** 입금자명 (기본값으로 기안자 이름을 사용) */
    @Column(name = "payer_name", length = 100, nullable = false)
    private String payerName;

    /** 요청 상세 내용 */
    @Lob
    @Column(name = "request_detail", columnDefinition = "TEXT")
    private String requestDetail;

    /** 영수증 파일 경로 */
    @Column(name = "receipt_file", length = 500)
    private String receiptFile;

    /** 결재 코드 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approval_code", nullable = false)
    private ApprovalCode approvalCode;

    /** 처리 완료 여부 */
    @Column(name = "is_completed")
    private Boolean isCompleted;

    /** 승인(또는 반려) 일시 */
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}
