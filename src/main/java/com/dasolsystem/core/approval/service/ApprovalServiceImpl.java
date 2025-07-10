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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;
    private final ApprovalCodeRepository approvalCodeRepository;

    @Transactional
    public Long postRequest(ApprovalRequestDto dto) {
        Member RequestMember = userRepository.findByStudentId(dto.getStudentId()).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"student not found"));
        List<Member> approverList = new ArrayList<>();
        for (String memberId : dto.getApproversId()) {
            Member member = userRepository.findById(Long.valueOf(memberId))
                    .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "none found member"));
            approverList.add(member);
        }
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .accountNumber(dto.getAccountNumber())
                .approvers(approverList)
                .payerName(dto.getPayerName())
                .receiptFile(dto.getReceiptFile())
                .requestDate(dto.getRequestDate())
                .requestDetail(dto.getRequestDetails())
                .requestedAmount(dto.getRequestAmount())
                .title(dto.getTitle())
                .approvalCode(
                        approvalCodeRepository.findByCode(dto.getApprovalCode()).orElseThrow(()-> new DBFaillException(ApiState.ERROR_500,"approval code not found"))
                )
                .member(RequestMember).build();
        return approvalRequestRepository.save(approvalRequest).getRequestId();
    }

    @Transactional
    public Long approveRequestAccept(ApprovalPostAcceptDto dto, String studentId) {
        Member requestUser = userRepository.findByStudentId(studentId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"student not found"));
        List<Member> approvalUser =approvalRequestRepository.findById(dto.getPostId()).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"Post not found")).getApprovers();
        if(!approvalUser.contains(requestUser)) throw new AuthFailException(ApiState.ERROR_700,"이 요청을 처리할 권한이 없습니다.");
        ApprovalRequest requestPost = approvalRequestRepository.findById(dto.getPostId()).orElseThrow(() -> new DBFaillException(ApiState.ERROR_500,"Post not found"));
        if(dto.isApproved()){
            requestPost.setApprovalDate(LocalDateTime.now());
            requestPost.setIsCompleted(true);
        }
        return requestPost.getRequestId();
    }

    @Transactional(readOnly = true)
    public GetApprovalPostResponse getApprovalPost(String studentId){
        Member approver = userRepository.findByStudentId(studentId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"student not found"));
        MemberDto memberDto = MemberDto.builder()
                .id(approver.getId())
                .studentId(approver.getStudentId())
                .name(approver.getName())
                .build();
        List<ApprovalRequest> approvalRequests = approver.getApprovalRequests();
        List<ApprovalRequestsDto> approvalRequestsDto = new ArrayList<>();
        for(ApprovalRequest approvalRequest : approvalRequests){
            approvalRequestsDto.add(
                    ApprovalRequestsDto.builder()
                            .approvalDate(approvalRequest.getApprovalDate())
                            .approvalCode(approvalRequest.getApprovalCode().getCode()+" "+approvalRequest.getApprovalCode().getName())
                            .isCompleted(approvalRequest.getIsCompleted())
                            .memberName(approvalRequest.getMember().getName())
                            .requestId(approvalRequest.getRequestId())
                            .accountNumber(approvalRequest.getAccountNumber())
                            .payerName(approvalRequest.getPayerName())
                            .receiptFile(approvalRequest.getReceiptFile())
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
}
