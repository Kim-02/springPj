package com.dasolsystem.core.tests;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Debug {
    @GetMapping("/debug")
    public String debug(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "✅ 인증 성공: " + authentication.getName() + ", 권한: " + authentication.getAuthorities();
        } else {
            return "❌ 인증 실패";
        }
    }
}
