// PaymentCheckController.java
package com.dasolsystem.core.upload.controller;

import com.dasolsystem.core.upload.dto.PaymentStatusDto;
import com.dasolsystem.core.upload.service.PaymentCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentCheckController {

    private final PaymentCheckService paymentCheckService;

    @GetMapping("/check")
    public ResponseEntity<List<PaymentStatusDto>> checkPaymentStatus(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String studentId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PaymentStatusDto> result = paymentCheckService.checkPaymentStatus(name, studentId);
        return ResponseEntity.ok(result);
    }
}
