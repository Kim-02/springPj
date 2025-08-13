package com.dasolsystem.core.approval.controller;


import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.CodeFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.config.excption.InvalidTokenException;
import com.dasolsystem.core.approval.dto.ApprovalPostAcceptDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestDto;
import com.dasolsystem.core.approval.dto.GetApprovalPostResponse;
import com.dasolsystem.core.approval.repository.ApprovalRequestRepository;
import com.dasolsystem.core.approval.service.ApprovalService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    //결재 신청
    @PostMapping("/post")
    public ResponseEntity<ResponseJson<?>> post(@ModelAttribute ApprovalRequestDto approvalRequestDto,HttpServletRequest request) throws IOException {
        if(!securityGuardian.userValidate(request,"Manager")) throw new InvalidTokenException(ApiState.ERROR_101,"권한을 확인하세요");
        approvalRequestDto.setStudentId(securityGuardian.getServletTokenClaims(request).getSubject());
        Long approvalId = approvalService.postRequest(approvalRequestDto);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success saving "+approvalId)
                        .build()
        );
    }

    /**
     *권한을 가진 유저가 승인이 가능하도록함
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/postAccept")
    public ResponseEntity<ResponseJson<?>> postAccept(@RequestBody ApprovalPostAcceptDto dto,
                                                      HttpServletRequest request) {
        if(!securityGuardian.userValidate(request,"Manager")) throw new InvalidTokenException(ApiState.ERROR_101,"권한을 확인하세요");
        String studentId = securityGuardian.getServletTokenClaims(request).getSubject();
        Long completeId = approvalService.approveRequestAccept(dto,studentId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("request saving "+completeId)
                        .build()
        );
    }

    //본인에게 온 결재 요청을 모두 확인할 수 있음
    @GetMapping("/getAcceptPost")
    public ResponseEntity<ResponseJson<?>> getAcceptPost(HttpServletRequest request) throws IOException {
        if(!securityGuardian.userValidate(request,"Manager")) throw new InvalidTokenException(ApiState.ERROR_101,"권한을 확인하세요");
        String studentId = securityGuardian.getServletTokenClaims(request).getSubject();
        GetApprovalPostResponse response = approvalService.getApprovalPost(studentId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success loading "+studentId)
                        .result(
                                response
                        )
                        .build()
        );
    }

    //모든 결재 요청을 확인할 수 있음
    @GetMapping("/getAllRequest")
    public ResponseEntity<ResponseJson<?>> getAllRequest() throws IOException {
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(
                                approvalService.getAllApprovalRequests()
                        )
                        .build()
        );
    }

    //회장 권한을 가지면 요청을 삭제할 수 있음
    @DeleteMapping("/deleteRequest/{postId}")
    @Transactional
    public ResponseEntity<ResponseJson<?>> deleteRequest(@PathVariable Long postId,HttpServletRequest request){
        if(!securityGuardian.userValidate(request,"Manager")) throw new InvalidTokenException(ApiState.ERROR_101,"권한을 확인하세요");
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
}

