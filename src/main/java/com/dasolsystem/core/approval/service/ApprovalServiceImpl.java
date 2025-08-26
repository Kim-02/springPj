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

    @Transactional
    public Long postRequest(ApprovalRequestDto dto) throws IOException {
        Member RequestMember = userRepository.findByStudentId(dto.getStudentId()).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"student not found"));
        List<Member> approverList = new ArrayList<>();
        for (String memberId : dto.getApproversId()) {
            Member member = userRepository.findById(Long.valueOf(memberId))
                    .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "none found member"));
            approverList.add(member);
        }

        String filePath = fileControlService.uploadFile(
                FileUploadDto.builder()
                        .file(dto.getReceiptFile())
                        .path("approval/"+dto.getStudentId())
                        .build()
        );
        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .accountNumber(dto.getAccountNumber())
                .approvers(approverList)
                .payerName(dto.getPayerName())
                .receiptFile(filePath)
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
        //출금 기록부
        transactionRecordService.expendRecordSave(ExpendTransactionDto.builder()
                        .amount(requestPost.getRequestedAmount())
                        .approvalRequest(requestPost)
                        .member(requestUser)
                        .code(requestPost.getApprovalCode().getName())
                .build());
        return requestPost.getRequestId();
    }

    @Transactional(readOnly = true)
    public GetApprovalPostResponse getApprovalPost(String studentId) throws IOException {
        Member approver = userRepository.findByStudentId(studentId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"student not found"));
        MemberDto memberDto = MemberDto.builder()
                .id(approver.getMemberId())
                .studentId(approver.getStudentId())
                .name(approver.getName())
                .build();
        List<ApprovalRequest> approvalRequests = approver.getApprovalRequests();
        List<ApprovalRequestsDto> approvalRequestsDto = new ArrayList<>();
        for(ApprovalRequest approvalRequest : approvalRequests){
            byte[] fileBytes = fileControlService.getFileBytes(approvalRequest.getReceiptFile());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            approvalRequestsDto.add(
                    ApprovalRequestsDto.builder()
                            .approvalDate(approvalRequest.getApprovalDate())
                            .approvalCode(approvalRequest.getApprovalCode().getCode()+" "+approvalRequest.getApprovalCode().getName())
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

    @Transactional(readOnly = true)
    public List<ApprovalAllPostViewDto> getAllApprovalRequests() throws IOException {
        List<ApprovalRequest> allApprovalRequests = approvalRequestRepository.findAll();
        List<ApprovalAllPostViewDto> approvalRequestDtos = new ArrayList<>();

        for(ApprovalRequest approvalRequest : allApprovalRequests){
            byte[] fileBytes = fileControlService.getFileBytes(approvalRequest.getReceiptFile());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            List<MemberDto> approvers = new ArrayList<>();
            for(Member member:approvalRequest.getApprovers()){
                approvers.add(
                        MemberDto.builder()
                                .id(member.getMemberId())
                                .name(member.getName())
                                .studentId(member.getStudentId())
                                .build()
                );
            }
            approvalRequestDtos.add(
                    ApprovalAllPostViewDto.builder()
                            .approvers(approvers)
                            .approvalRequests(
                                    ApprovalRequestsDto.builder()
                                            .requestId(approvalRequest.getRequestId())
                                            .memberName(approvalRequest.getMember().getName())
                                            .requestDate(approvalRequest.getRequestDate())
                                            .title(approvalRequest.getTitle())
                                            .requestedAmount(approvalRequest.getRequestedAmount())
                                            .accountNumber(approvalRequest.getAccountNumber())
                                            .payerName(approvalRequest.getPayerName())
                                            .requestDetail(approvalRequest.getRequestDetail())
                                            .approvalCode(approvalRequest.getApprovalCode().getCode()+" "+approvalRequest.getApprovalCode().getName())
                                            .isCompleted(approvalRequest.getIsCompleted())
                                            .approvalDate(approvalRequest.getApprovalDate())
                                            .build()
                            )
                            .byteFile(base64)
                            .build()
            );
        }
        return approvalRequestDtos;
    }

//    @Transactional(readOnly = true)
//    public List<ApprovalAllPostViewDto> getTitleApprovalRequests() throws IOException {
//        List<ApprovalRequest> allApprovalRequests = approvalRequestRepository.findAll();
//    }
}
