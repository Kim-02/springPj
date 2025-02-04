package com.dasolsystem.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    // 기본페이지 요청 메서드
    @GetMapping("/")
    public String index() {
        return "dasoltest";
    }

}