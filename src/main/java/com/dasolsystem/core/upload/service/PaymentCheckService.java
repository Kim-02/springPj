package com.dasolsystem.core.upload.service;

import com.dasolsystem.core.upload.dto.PaymentStatusDto;

import java.util.List;

public interface PaymentCheckService {
    List<PaymentStatusDto> checkPaymentStatus(String name, String studentId);
}
