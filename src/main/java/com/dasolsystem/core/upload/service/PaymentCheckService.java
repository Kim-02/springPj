package com.dasolsystem.core.upload.service;

import com.dasolsystem.core.upload.dto.PaymentStatusDto;

public interface PaymentCheckService {
    PaymentStatusDto checkPaymentStatus(String name, String studentId);
}
