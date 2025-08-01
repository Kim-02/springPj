package com.dasolsystem.core.upload.controller;

import com.dasolsystem.core.upload.dto.PaymentStatusDto;
import com.dasolsystem.core.upload.service.PaymentCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentCheckController {

    private final PaymentCheckService paymentCheckService;

    @GetMapping("/check")
    public PaymentStatusDto checkPaymentStatus(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String studentId
    ) {
        return paymentCheckService.checkPaymentStatus(name, studentId);
    }
}
