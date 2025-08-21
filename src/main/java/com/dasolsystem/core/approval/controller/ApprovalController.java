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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
     * 월별 결재 목록 조회 API
     * - 특정 연월(month)에 해당하는 모든 결재 요청을 리스트로 반환한다.
     * - 이미지(Base64)는 제외 → 목록 응답을 가볍게 유지.
     * - 페이징을 사용하지 않고, 월 전체 데이터를 한 번에 내려준다.
     *
     * [요청 예시]
     *   GET /api/approval/monthly?month=2025-08
     *
     * [파라미터]
     * @param month   : 조회할 연월 (예: "2025-08")
     * @param request : 사용자 권한 검증용 (Manager 권한 확인)
     *
     * [동작 순서]
     * 1) 요청 사용자의 권한 검증 (Manager 아니면 예외 발생)
     * 2) ApprovalService.getMonthlyApprovals 호출 → List<ApprovalRequestsDto> 반환
     * 3) ResponseJson 포맷으로 감싸 클라이언트에게 전달
     *
     * [응답 예시]
     * {
     *   "status": 200,
     *   "message": "success monthly 2025-08",
     *   "result": [ { ...결재요청DTO... }, {...}, ... ]
     * }
     */
    @GetMapping("/monthly")
    public ResponseEntity<ResponseJson<?>> getMonthly(@RequestParam String month,
                                                      HttpServletRequest request) {
        // 권한 검증 ("Manager")
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }

        // 서비스 호출 → 전체 리스트(List<ApprovalRequestsDto>) 반환
        List<ApprovalRequestsDto> result = approvalService.getMonthlyApprovals(month);

        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success monthly " + month)
                        .result(result) // List 반환.
                        .build()
        );
    }

    /**
     * 결재 요청 단건 상세 조회 (이미지 포함 + 승인자 목록 포함)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseJson<?>> getDetail(@PathVariable Long id,
                                                     HttpServletRequest request) throws IOException {
        // 권한 검증 ("Manager")
        if (!securityGuardian.userValidate(request, "Manager")) {
            throw new InvalidTokenException(ApiState.ERROR_101, "권한을 확인하세요");
        }

        ApprovalAllPostViewDto dto = approvalService.getApprovalDetail(id);

        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success detail " + id)
                        .result(dto)
                        .build()
        );
    }
}
