package com.dasolsystem.core.jwt.controller;

import com.dasolsystem.core.jwt.util.JwtBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final JwtBuilder jwtBuilder;
    @PostMapping("/generate")
    public String jwtTokenGenerate(){

        return "";
    }
}
