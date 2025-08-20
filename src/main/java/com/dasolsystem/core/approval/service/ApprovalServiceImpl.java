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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
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

        // 지출 기록 저장
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
     * - 기존 로직 유지
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
     * 월별 목록 (이미지 제외, 페이지네이션)
     * - month: "YYYY-MM" 형식, 예) 2025-08
     * - KST(서버 로컬 타임) 기준으로 월 경계 [start, end] 생성
     * - 정렬: requestDate DESC
     * <p>
     * [주의]
     * - month 파싱 실패 시 IllegalArgumentException 발생 → 컨트롤러 계층에서 400으로 매핑 권장
     * - 이미지(Base64) 등 대용량은 포함하지 않으므로 목록 응답에 적합
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ApprovalRequestsDto> getMonthlyApprovals(String month, int page, int size) {
        // 1) month 문자열을 YearMonth로 파싱 (형식 고정: "YYYY-MM")
        //    - 예외: 형식 불일치 → IllegalArgumentException으로 래핑
        YearMonth ym;
        try {
            ym = YearMonth.parse(month); // "2025-08" → YearMonth(2025-08)
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("month는 'YYYY-MM' 형식이어야 합니다. 예) 2025-08");
        }

        // 2) 해당 월의 시작/끝 경계를 LocalDateTime으로 계산
        //    - start: 월 첫날 00:00:00.000
        //    - end  : 월 마지막날 23:59:59.999999999 (LocalTime.MAX)
        //    - 서버 로컬 타임 기준(KST 가정). 타임존 이슈 있으면 ZonedDateTime로 승격 고려.
        LocalDateTime start = LocalDateTime.of(ym.atDay(1), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(ym.atEndOfMonth(), LocalTime.MAX);

        // 3) 페이지네이션 + 정렬조건 정의
        //    - page: 0-based
        //    - size: 페이지 당 로우 수
        //    - 정렬: 요청일(requestDate) 내림차순(최신 우선)
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));

        // 4) 레포지토리 조회
        //    - 기간 필터: [start, end] 사이의 requestDate
        //    - 반환: Page<ApprovalRequest>
        Page<ApprovalRequest> entityPage =
                approvalRequestRepository.findByRequestDateBetween(start, end, pageable);

        // 5) 엔티티 → DTO 매핑 (이미지 제외)
        //    - 각 필드 설명
        //      * requestId        : 결재요청 PK
        //      * memberName       : 요청자 이름 (ar.getMember().getName())
        //      * requestDate      : 요청일시
        //      * title            : 제목
        //      * requestedAmount  : 요청금액
        //      * accountNumber    : 계좌번호
        //      * payerName        : 입금자명
        //      * requestDetail    : 상세내용
        //      * approvalCode     : "코드명 코드설명" 조합 문자열
        //      * isCompleted      : 결재완료 여부
        //      * approvalDate     : 결재일(완료 시)
        List<ApprovalRequestsDto> content = new ArrayList<>();
        for (ApprovalRequest ar : entityPage.getContent()) {
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

        // 6) PageImpl로 래핑하여 동일한 페이징 메타데이터(totalElements 등) 유지
        return new PageImpl<>(content, pageable, entityPage.getTotalElements());
    }

    /**
     * 단건 상세 (이미지 Base64 + 승인자 목록 포함)
     * <p>
     * [동작]
     * 1) requestId로 ApprovalRequest 엔티티 조회(없으면 DBFaillException)
     * 2) 저장된 파일 경로(receiptFile)로 바이너리 로드 → BufferedImage → JPG로 재인코딩 → Base64 문자열 생성
     * 3) 승인자(approvers) 목록을 MemberDto 리스트로 변환
     * 4) 본문(ApprovalRequestsDto) 구성 (이미지 제외)
     * 5) ApprovalAllPostViewDto로 합쳐 반환 (본문 + 승인자 + Base64 이미지)
     * <p>
     * [주의]
     * - 파일 포맷을 'jpg'로 고정 인코딩 중 → 원본 포맷 보존 필요 시 확장자/미디어타입 보관 및 동적 변환 고려
     * - 대용량 이미지의 Base64는 응답 사이즈 증가 → 썸네일/지연로드 고려
     */
    @Transactional(readOnly = true)
    @Override
    public ApprovalAllPostViewDto getApprovalDetail(Long requestId) throws IOException {
        // 1) 단건 조회 (없으면 500 커스텀 예외)
        ApprovalRequest ar = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "ApprovalRequest not found"));

        // 2) 파일 경로 기반으로 실제 이미지 바이트 로딩
        //    - fileControlService.getFileBytes(path): 스토리지(S3 등)에서 파일 바이트 획득
        byte[] fileBytes = fileControlService.getFileBytes(ar.getReceiptFile());

        // 3) 바이트 → BufferedImage 파싱 (실패 시 null 가능 → NPE 방지 필요)
        //    - 만약 읽기 실패/비지원 포맷이라면 IOException 발생 가능
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));

        // 4) BufferedImage → JPG 재인코딩 → Base64 인코딩
        //    - 인코딩 포맷은 'jpg'로 고정 (원본이 png라도 jpg로 변환됨)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        // 5) 승인자 목록을 경량 DTO로 변환 (id / studentId / name)
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

        // 6) 본문(단건) DTO 구성 (이미지 제외)
        //    - 목록 DTO와 동일 구조이되, 단건 상세에 필요한 필드들 중심
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

        // 7) 상세 응답 DTO 조립: 승인자 + 본문 + Base64 이미지(JPG)
        return ApprovalAllPostViewDto.builder()
                .approvers(approvers)
                .approvalRequests(body)
                .byteFile(base64)
                .build();
    }
}

