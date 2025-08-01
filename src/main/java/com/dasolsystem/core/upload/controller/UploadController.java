package com.dasolsystem.core.upload.controller;

import com.dasolsystem.core.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    /**
     * 회원 명단 + 납부 명단 엑셀 업로드 처리
     */
    @PostMapping("/member-payment")
    public ResponseEntity<String> uploadMemberAndPayment(
            @RequestParam("memberFile") MultipartFile memberFile,
            @RequestParam("paymentFile") MultipartFile paymentFile
    ) {
        try {
            uploadService.processExcelFiles(memberFile, paymentFile);
            return ResponseEntity.ok("✅ 회원 업로드 및 납부 처리 완료");
        } catch (Exception e) {
            log.error("❌ 업로드 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("❌ 업로드 실패: " + e.getMessage());
        }
    }
}
