package com.dasolsystem.core.approval.controller;

import com.dasolsystem.config.excption.InvalidTokenException;
import com.dasolsystem.core.approval.dto.ApprovalAllPostViewDto;
import com.dasolsystem.core.approval.dto.ApprovalPostAcceptDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestsDto;
import com.dasolsystem.core.approval.repository.ApprovalRequestRepository;
import com.dasolsystem.core.approval.service.ApprovalService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final SecurityGuardian securityGuardian;
    private final ApprovalService approvalService;
    private final ApprovalRequestRepository approvalRequestRepository;

    // 결재 신청
    @PostMapping("/post")
    public ResponseEntity<ResponseJson<?>> post(@ModelAttribute ApprovalRequestDto approvalRequestDto,
                                                HttpServletRequest request) throws IOException {
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }
        Long approvalId = approvalService.postRequest(approvalRequestDto);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success saving " + approvalId)
                        .build()
        );
    }

    /**
     * 권한을 가진 유저가 승인이 가능하도록 함
     */
    @PostMapping("/postAccept")
    public ResponseEntity<ResponseJson<?>> postAccept(@RequestBody ApprovalPostAcceptDto dto,
                                                      HttpServletRequest request) {
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }
        String studentId = securityGuardian.getServletTokenClaims(request).getSubject();
        Long completeId = approvalService.approveRequestAccept(dto, studentId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("request saving " + completeId)
                        .build()
        );
    }

    // 본인에게 온 결재 요청을 모두 확인 (이미지 포함)
    @GetMapping("/getAcceptPost")
    public ResponseEntity<ResponseJson<?>> getAcceptPost(HttpServletRequest request) throws IOException {
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }
        String studentId = securityGuardian.getServletTokenClaims(request).getSubject();
        var response = approvalService.getApprovalPost(studentId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success loading " + studentId)
                        .result(response)
                        .build()
        );
    }

    // 모든 결재 요청 조회 (이미지 포함)
    @GetMapping("/getAllRequest")
    public ResponseEntity<ResponseJson<?>> getAllRequest() throws IOException {
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(approvalService.getAllApprovalRequests())
                        .build()
        );
    }

    // 요청 삭제 (권한 체크)
    @DeleteMapping("/deleteRequest/{postId}")
    @Transactional
    public ResponseEntity<ResponseJson<?>> deleteRequest(@PathVariable Long postId,
                                                         HttpServletRequest request) {
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }
        if (!approvalRequestRepository.existsById(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseJson.builder()
                            .status(404)
                            .message("삭제할 요청을 찾을 수 없습니다: " + postId)
                            .build());
        }
        approvalRequestRepository.deleteById(postId);
        return ResponseEntity.noContent().build();
    }



    /**
     * 월별 결재 목록 조회 (이미지 제외, 페이지네이션 지원)
     *
     * [기능 설명]
     * - 특정 월(month)에 해당하는 모든 결재 요청을 조회한다.
     * - 결재 요청에 첨부된 이미지 데이터는 제외하고, 목록/요약 정보만 반환한다.
     * - Spring Data JPA의 Page 객체를 그대로 반환하므로, 클라이언트는
     *   content(결재 요청 리스트), totalPages, totalElements, size, number 등의 정보를 함께 확인할 수 있다.
     *
     * [요청 예시]
     *   GET /api/approval/monthly?month=2025-08&page=0&size=20
     *
     * [파라미터 설명]
     * @param month : 조회할 연월 (예: "2025-08"). 내부적으로 Service 계층에서 파싱하여 조건 검색 수행.
     * @param page  : 조회할 페이지 번호 (0부터 시작). 기본값 0.
     * @param size  : 페이지당 가져올 데이터 수. 기본값 20.
     * @param request : HttpServletRequest 객체, 사용자 권한 검증(securityGuardian)에서 사용.
     *
     * [권한]
     * - "Manager" 권한을 가진 사용자만 접근 가능.
     *   → 검증 실패 시 InvalidTokenException 발생 (ApiState.ERROR_101)
     *
     * [반환 값]
     * - ResponseEntity<ResponseJson<?>>
     *   → status : 200
     *   → message : "success monthly {month}"
     *   → result : Page<ApprovalRequestsDto>
     *     (결재 요청 DTO 리스트 + 페이지 메타데이터 포함)
     */
    @GetMapping("/monthly")
    public ResponseEntity<ResponseJson<?>> getMonthly(@RequestParam String month,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size,
                                                      HttpServletRequest request) {
        // 1. 사용자 권한 검증 ("Manager" 권한 필요)
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }

        // 2. Service 계층 호출 → 특정 월의 결재 요청 목록을 Page 형태로 반환
        Page<ApprovalRequestsDto> result = approvalService.getMonthlyApprovals(month, page, size);

        // 3. 정상 응답 반환
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success monthly " + month) // ex) success monthly 2025-08
                        .result(result) // Page 구조 그대로 전달 (content / totalPages / totalElements 등)
                        .build()
        );
    }

    /**
     * 결재 요청 단건 상세 조회
     *
     * [기능 설명]
     * - 특정 결재 요청 ID를 기준으로 상세 정보를 조회한다.
     * - 첨부 이미지(영수증 등)는 Base64 문자열로 변환되어 포함된다.
     * - 해당 요청의 승인자 목록(결재 라인) 정보도 함께 반환한다.
     *
     * [요청 예시]
     *   GET /api/approval/15   (id=15인 결재 요청 상세 조회)
     *
     * [파라미터 설명]
     * @param id      : 상세 조회할 결재 요청 ID
     * @param request : HttpServletRequest 객체, 사용자 권한 검증(securityGuardian)에서 사용
     *
     * [권한]
     * - "Manager" 권한을 가진 사용자만 접근 가능.
     *   → 검증 실패 시 InvalidTokenException 발생 (ApiState.ERROR_101)
     *
     * [반환 값]
     * - ResponseEntity<ResponseJson<?>>
     *   → status : 200
     *   → message : "success detail {id}"
     *   → result : ApprovalAllPostViewDto
     *     (결재 요청 단건의 상세 DTO, Base64 이미지 + 승인자 목록 포함)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseJson<?>> getDetail(@PathVariable Long id,
                                                     HttpServletRequest request) throws IOException {
        // 1. 사용자 권한 검증 ("Manager" 권한 필요)
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }

        // 2. Service 계층 호출 → 결재 요청 상세 정보 조회
        ApprovalAllPostViewDto dto = approvalService.getApprovalDetail(id);

        // 3. 정상 응답 반환
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success detail " + id) // ex) success detail 15
                        .result(dto) // 단건 DTO (Base64 이미지 + 승인자 목록 포함)
                        .build()
        );
    }
}
