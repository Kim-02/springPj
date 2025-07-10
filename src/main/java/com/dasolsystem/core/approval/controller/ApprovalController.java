package com.dasolsystem.core.approval.controller;


import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.CodeFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.config.excption.InvalidTokenException;
import com.dasolsystem.core.approval.dto.ApprovalPostAcceptDto;
import com.dasolsystem.core.approval.dto.ApprovalRequestDto;
import com.dasolsystem.core.approval.dto.GetApprovalPostResponse;
import com.dasolsystem.core.approval.service.ApprovalService;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {
    private final SecurityGuardian securityGuardian;
    private final ApprovalService approvalService;

    @PostMapping("/post")
    public ResponseEntity<ResponseJson<?>> post(@RequestBody ApprovalRequestDto approvalRequestDto,HttpServletRequest request) {
        if(!securityGuardian.userValidate(request,"Manager")) throw new InvalidTokenException(ApiState.ERROR_101,"권한을 확인하세요");
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

    @GetMapping("/getAcceptPost")
    public ResponseEntity<ResponseJson<?>> getAcceptPost(HttpServletRequest request) {
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

    
}

