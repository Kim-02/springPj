package com.dasolsystem.core.mainpage;

import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainPage {
    @GetMapping("/api/main")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseJson<Object>> mainpage() {
        log.info("✅ mainpage 접근");
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result("Welcome main page.")
                        .build()
        );
    }
}
