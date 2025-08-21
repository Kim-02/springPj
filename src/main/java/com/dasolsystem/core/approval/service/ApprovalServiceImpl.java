package com.dasolsystem.core.approval.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.approval.dto.*;
import com.dasolsystem.core.approval.repository.ApprovalCodeRepository;
import com.dasolsystem.core.approval.repository.ApprovalRequestRepository;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.ApprovalRequest;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.file.FileControlService;
import com.dasolsystem.core.file.dto.FileUploadDto;
import com.dasolsystem.core.trasaction.dto.ExpendTransactionDto;
import com.dasolsystem.core.trasaction.service.TransactionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;
    private final ApprovalCodeRepository approvalCodeRepository;
    private final FileControlService fileControlService;
    private final TransactionRecordService transactionRecordService;

    /**
     * 결재 신청 저장
     */
    @Transactional
    @Override
    public Long postRequest(ApprovalRequestDto dto) throws IOException {
        // 요청자 조회
        Member requestMember = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "student not found"));

        // 승인자 목록 구성
        List<Member> approverList = new ArrayList<>();
        for (String memberId : dto.getApproversId()) {
            Member member = userRepository.findById(Long.valueOf(memberId))
                    .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "none found member"));
            approverList.add(member);
        }

        // 영수증 파일 업로드 (경로: approval/{studentId}/...)
        String filePath = fileControlService.uploadFile(
                FileUploadDto.builder()
                        .file(dto.getReceiptFile())
                        .path("approval/" + dto.getStudentId())
                        .build()
        );

        // ApprovalCode 매핑
        var approvalCode = approvalCodeRepository.findByCode(dto.getApprovalCode())
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "approval code not found"));

        // 엔티티 생성 후 저장
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .accountNumber(dto.getAccountNumber())
                .approvers(approverList)
                .payerName(dto.getPayerName())
                .receiptFile(filePath)
                .requestDate(dto.getRequestDate())
                .requestDetail(dto.getRequestDetails())
                .requestedAmount(dto.getRequestAmount())
                .title(dto.getTitle())
                .approvalCode(approvalCode)
                .member(requestMember)
                .build();

        return approvalRequestRepository.save(approvalRequest).getRequestId();
    }

    /**
     * 승인 처리 (승인자 권한 체크 포함) + 지출 기록 저장
     */
    @Transactional
    @Override
    public Long approveRequestAccept(ApprovalPostAcceptDto dto, String studentId) {
        // 승인자(요청 수행자) 조회
        Member requestUser = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "student not found"));

        ApprovalRequest requestPost = approvalRequestRepository.findById(dto.getPostId())
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "Post not found"));

        // 권한 체크: 해당 요청의 approvers 목록에 포함되어 있어야 함
        List<Member> approvalUsers = requestPost.getApprovers();
        if (!approvalUsers.contains(requestUser)) {
            throw new AuthFailException(ApiState.ERROR_700, "이 요청을 처리할 권한이 없습니다.");
        }

        // 승인 처리 (승인일/완료 여부)
        if (dto.isApproved()) {
            requestPost.setApprovalDate(LocalDateTime.now());
            requestPost.setIsCompleted(true);
        }

        // 지출 기록 저장 (현재 로직 유지: 승인 여부와 무관하게 저장)
        transactionRecordService.expendRecordSave(
                ExpendTransactionDto.builder()
                        .amount(requestPost.getRequestedAmount())
                        .approvalRequest(requestPost)
                        .member(requestUser)
                        .code(requestPost.getApprovalCode().getName())
                        .build()
        );

        return requestPost.getRequestId();
    }

    /**
     * "나에게 온" 결재 요청 조회 (이미지 포함)
     */
    @Transactional(readOnly = true)
    @Override
    public GetApprovalPostResponse getApprovalPost(String studentId) throws IOException {
        Member approver = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "student not found"));

        MemberDto memberDto = MemberDto.builder()
                .id(approver.getMemberId())
                .studentId(approver.getStudentId())
                .name(approver.getName())
                .build();

        List<ApprovalRequest> approvalRequests = approver.getApprovalRequests();
        List<ApprovalRequestsDto> approvalRequestsDto = new ArrayList<>();

        for (ApprovalRequest approvalRequest : approvalRequests) {
            // 파일 -> Base64
            byte[] fileBytes = fileControlService.getFileBytes(approvalRequest.getReceiptFile());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            approvalRequestsDto.add(
                    ApprovalRequestsDto.builder()
                            .approvalDate(approvalRequest.getApprovalDate())
                            .approvalCode(approvalRequest.getApprovalCode().getCode() + " " + approvalRequest.getApprovalCode().getName())
                            .isCompleted(approvalRequest.getIsCompleted())
                            .memberName(approvalRequest.getMember().getName())
                            .requestId(approvalRequest.getRequestId())
                            .accountNumber(approvalRequest.getAccountNumber())
                            .payerName(approvalRequest.getPayerName())
                            .receiptFile(base64)
                            .requestDetail(approvalRequest.getRequestDetail())
                            .title(approvalRequest.getTitle())
                            .requestDate(approvalRequest.getRequestDate())
                            .requestedAmount(approvalRequest.getRequestedAmount())
                            .build()
            );
        }

        return GetApprovalPostResponse.builder()
                .approvalRequests(approvalRequestsDto)
                .approver(memberDto)
                .build();
    }

    /**
     * 모든 결재 요청 조회 (이미지 포함)
     */
    @Transactional(readOnly = true)
    @Override
    public List<ApprovalAllPostViewDto> getAllApprovalRequests() throws IOException {
        List<ApprovalRequest> allApprovalRequests = approvalRequestRepository.findAll();
        List<ApprovalAllPostViewDto> approvalRequestDtos = new ArrayList<>();

        for (ApprovalRequest approvalRequest : allApprovalRequests) {
            // 파일 -> Base64
            byte[] fileBytes = fileControlService.getFileBytes(approvalRequest.getReceiptFile());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            // 승인자 DTO 목록
            List<MemberDto> approvers = new ArrayList<>();
            for (Member member : approvalRequest.getApprovers()) {
                approvers.add(
                        MemberDto.builder()
                                .id(member.getMemberId())
                                .name(member.getName())
                                .studentId(member.getStudentId())
                                .build()
                );
            }

            // 본문 DTO
            ApprovalRequestsDto body = ApprovalRequestsDto.builder()
                    .requestId(approvalRequest.getRequestId())
                    .memberName(approvalRequest.getMember().getName())
                    .requestDate(approvalRequest.getRequestDate())
                    .title(approvalRequest.getTitle())
                    .requestedAmount(approvalRequest.getRequestedAmount())
                    .accountNumber(approvalRequest.getAccountNumber())
                    .payerName(approvalRequest.getPayerName())
                    .requestDetail(approvalRequest.getRequestDetail())
                    .approvalCode(approvalRequest.getApprovalCode().getCode() + " " + approvalRequest.getApprovalCode().getName())
                    .isCompleted(approvalRequest.getIsCompleted())
                    .approvalDate(approvalRequest.getApprovalDate())
                    .build();

            approvalRequestDtos.add(
                    ApprovalAllPostViewDto.builder()
                            .approvers(approvers)
                            .approvalRequests(body)
                            .byteFile(base64)
                            .build()
            );
        }
        return approvalRequestDtos;
    }

    /**
     * 월별 목록 (이미지 제외, 비페이징 전체 리스트)
     * - month: "YYYY-MM" 형식, 예) 2025-08
     * - 정렬: requestDate DESC (최신 결재 요청부터)
     *
     * [동작 순서]
     * 1) 파라미터 문자열 month를 YearMonth로 파싱한다. (형식이 다르면 IllegalArgumentException 발생)
     * 2) 해당 월의 시작/끝 날짜(LocalDateTime) 범위를 계산한다.
     *    - start : 월 첫날 00:00:00
     *    - end   : 월 마지막날 23:59:59
     * 3) DB에서 해당 범위에 속하는 ApprovalRequest 엔티티 전체를 최신순으로 조회한다.
     *    - Repository 메서드: findByRequestDateBetweenOrderByRequestDateDesc
     *    - 페이징 없음, 전부 가져옴
     * 4) 엔티티를 DTO(ApprovalRequestsDto) 리스트로 변환한다.
     *    - 이미지(Base64)는 제외 → 목록 조회라 가볍게 유지
     * 5) 변환된 리스트를 그대로 반환한다.
     */
    @Transactional(readOnly = true)
    @Override
    public List<ApprovalRequestsDto> getMonthlyApprovals(String month) {
        // 1) month 문자열을 YearMonth로 파싱 (형식 고정: "YYYY-MM")
        YearMonth ym;
        try {
            ym = YearMonth.parse(month); // "2025-08" → YearMonth(2025-08)
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("month는 'YYYY-MM' 형식이어야 합니다. 예) 2025-08");
        }

        // 2) 해당 월의 시작/끝 경계를 LocalDateTime으로 계산 (서버 로컬 기준 유지)
        LocalDateTime start = LocalDateTime.of(ym.atDay(1), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(ym.atEndOfMonth(), LocalTime.MAX);

        // 3) 리포지토리 전체 조회(최신순)
        List<ApprovalRequest> entities =
                approvalRequestRepository.findByRequestDateBetweenOrderByRequestDateDesc(start, end);

        // 4) 엔티티 → DTO 매핑 (이미지 제외)
        List<ApprovalRequestsDto> content = new ArrayList<>(entities.size());
        for (ApprovalRequest ar : entities) {
            content.add(
                    ApprovalRequestsDto.builder()
                            .requestId(ar.getRequestId())
                            .memberName(ar.getMember().getName())
                            .requestDate(ar.getRequestDate())
                            .title(ar.getTitle())
                            .requestedAmount(ar.getRequestedAmount())
                            .accountNumber(ar.getAccountNumber())
                            .payerName(ar.getPayerName())
                            .requestDetail(ar.getRequestDetail())
                            .approvalCode(ar.getApprovalCode().getCode() + " " + ar.getApprovalCode().getName())
                            .isCompleted(ar.getIsCompleted())
                            .approvalDate(ar.getApprovalDate())
                            .build()
            );
        }

        return content;
    }

    /**
     * 단건 상세 (이미지 Base64 + 승인자 목록 포함)
     */
    @Transactional(readOnly = true)
    @Override
    public ApprovalAllPostViewDto getApprovalDetail(Long requestId) throws IOException {
        // 1) 단건 조회
        ApprovalRequest ar = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "ApprovalRequest not found"));

        // 2) 파일 경로 기반 이미지 바이트 로딩
        byte[] fileBytes = fileControlService.getFileBytes(ar.getReceiptFile());

        // 3) 바이트 → BufferedImage → JPG → Base64
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        // 4) 승인자 목록 DTO
        List<MemberDto> approvers = new ArrayList<>();
        for (Member m : ar.getApprovers()) {
            approvers.add(
                    MemberDto.builder()
                            .id(m.getMemberId())
                            .studentId(m.getStudentId())
                            .name(m.getName())
                            .build()
            );
        }

        // 5) 본문 DTO
        ApprovalRequestsDto body = ApprovalRequestsDto.builder()
                .requestId(ar.getRequestId())
                .memberName(ar.getMember().getName())
                .requestDate(ar.getRequestDate())
                .title(ar.getTitle())
                .requestedAmount(ar.getRequestedAmount())
                .accountNumber(ar.getAccountNumber())
                .payerName(ar.getPayerName())
                .requestDetail(ar.getRequestDetail())
                .approvalCode(ar.getApprovalCode().getCode() + " " + ar.getApprovalCode().getName())
                .isCompleted(ar.getIsCompleted())
                .approvalDate(ar.getApprovalDate())
                .build();

        // 6) 합쳐서 반환
        return ApprovalAllPostViewDto.builder()
                .approvers(approvers)
                .approvalRequests(body)
                .byteFile(base64)
                .build();
    }
}
