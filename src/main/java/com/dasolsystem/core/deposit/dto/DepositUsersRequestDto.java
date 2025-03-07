package com.dasolsystem.core.deposit.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositUsersRequestDto {
    private MultipartFile file;
    private String depositType;
    private Integer selectAmount;
}
