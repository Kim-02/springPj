package com.dasolsystem.core.auth.user.controller;

import com.dasolsystem.core.auth.user.service.PaidMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paid-member")
@RequiredArgsConstructor
public class PaidMemberController {

    private final PaidMemberService paidMemberService;

    // 학생회비 납부 업데이트 실행
    @PostMapping("/update")
    public String updatePaidMembers() {
        paidMemberService.updatePaidMembers();
        return "Paid members updated successfully!";
    }
}