package com.dasolsystem.core.auth.userdetail.controller;

import com.dasolsystem.core.guardian.SecurityGuardian;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
@AllArgsConstructor
public class UserStatusCheckController {
    private final SecurityGuardian securityGuardian;

    /**
     * 프론트에서 유저의 권한을 확인하기 위해 사용
     * @param request 유저 토큰
     * @return String Role
     */
    @GetMapping("/role")
    public String getRole(HttpServletRequest request) {
        Claims accessClaim = securityGuardian.getServletTokenClaims(request);
        return accessClaim.get("role", String.class);
    }
}
