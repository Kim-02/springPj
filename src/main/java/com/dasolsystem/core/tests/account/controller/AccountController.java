package com.dasolsystem.core.tests.account.controller;


import com.dasolsystem.core.tests.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/test")
    public String account(@RequestParam String name) {
        Long stime = System.nanoTime();
        String response = accountService.getMessageByName(name);
        Long etime = System.nanoTime();
        System.out.println("✅ Execution Time (ns): "+(etime-stime)+" ns");
        return response;
    }
}
