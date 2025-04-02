package com.dasolsystem.core.approval.dto;

import com.dasolsystem.core.entity.Users;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalPostDto {
    private String title;
    private String drafterName;
    private List<Users> approvalUsers;
    private Integer deposit;
    private String accountNumber;
    private String depositer;
    private String description;
    private String approvalCode;
    private MultipartFile receipt;
}
