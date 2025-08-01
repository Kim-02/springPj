package com.dasolsystem.core.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusDto {
    private String name;
    private String studentId;
    private Boolean paid;
}
