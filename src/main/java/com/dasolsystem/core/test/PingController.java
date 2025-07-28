package com.dasolsystem.core.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class PingController {
    @GetMapping("/ping")
    public ResponseEntity<PingResponseDto> ping() {
        PingResponseDto pingResponseDto = new PingResponseDto(Instant.now().toString());
        return ResponseEntity.ok(pingResponseDto);
    }
}
