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
     * 전체 회원 명단 엑셀 업로드 처리
     * 엑셀 형식: No., 학번, 이름, 성별, 휴대전화, 학적상태, 학과, 학년
     */
    @PostMapping("/member")
    public ResponseEntity<String> uploadMemberExcel(@RequestParam("memberFile") MultipartFile memberFile) {
        try {
            uploadService.processExcelFile(memberFile);
            return ResponseEntity.ok("✅ 회원 업로드 완료");
        } catch (Exception e) {
            log.error("❌ 회원 업로드 실패", e);
            return ResponseEntity.internalServerError().body("❌ 업로드 실패: " + e.getMessage());
        }
    }
}
