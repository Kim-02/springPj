package com.dasolsystem.core.approval.service;

import com.dasolsystem.config.S3Uploader;
import com.dasolsystem.config.excption.FileException;
import com.dasolsystem.core.approval.dto.ApprovalPostDto;
import com.dasolsystem.core.approval.dto.ApprovalSummaryDto;
import com.dasolsystem.core.approval.repository.ApprovalRepository;
import com.dasolsystem.core.entity.Approval;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.ApiState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final S3Uploader s3Uploader;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Transactional
    public Long saveApprovePost(ApprovalPostDto postDto) throws IOException {
        // ✅ 1. S3에 영수증 업로드
        String receiptUrl = null;
        MultipartFile file = postDto.getReceipt();

        if (file != null && !file.isEmpty()) {
            try {
                receiptUrl = s3Uploader.upload(file, file.getName());
            } catch (IOException e) {
                throw new FileException(ApiState.ERROR_801,"파일 업로드에 실패했습니다.");
            }
        }

        // ✅ 2. Approval 엔티티 생성 및 저장
        Approval approval = Approval.builder()
                .approvalDate(LocalDateTime.now())
                .title(postDto.getTitle())
                .drafterName(postDto.getDrafterName())
                .approvalUsers(postDto.getApprovalUsers())
                .deposit(postDto.getDeposit())
                .accountNumber(postDto.getAccountNumber())
                .depositer(postDto.getDepositer())
                .description(postDto.getDescription())
                .approvalCode(postDto.getApprovalCode())
                .receiptUrl(receiptUrl) // ✅ URL 저장
                .build();

        approvalRepository.save(approval); // ✅ 저장
        return approval.getId();
    }

    @Transactional(readOnly = true)
    public List<ApprovalSummaryDto> getApprovalSummaries() {
        return approvalRepository.findAll(Sort.by(Sort.Direction.ASC, "approvalDate"))
                .stream()
                .map(approval -> {
                    List<String> approvers = approval.getApprovalUsers().stream()
                            .map(Users::getName) // Users 엔티티에 getName()이 있다고 가정
                            .toList();

                    String status = Boolean.TRUE.equals(approval.getApproved()) ? "승인" : "기안중";

                    return new ApprovalSummaryDto(
                            approval.getApprovalDate(),
                            approval.getDrafterName(),
                            approvers.get(0),
                            approval.getApprovalCode(),
                            approval.getTitle(),
                            status
                    );
                })
                .collect(Collectors.toList());
    }
}
