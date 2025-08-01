package com.dasolsystem.core.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 납부 명단 엑셀용 DTO
 * 엑셀 헤더 순서: 이름, 금액
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentExcelDto {
    private String name;
    private int amount;
}
