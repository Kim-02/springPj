package com.dasolsystem.core.mainPage;

import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainPage {
    private JwtBuilder jwtBuilder;
    @GetMapping("/api/main")
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
