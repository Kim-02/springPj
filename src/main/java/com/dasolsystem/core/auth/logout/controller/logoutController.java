package com.dasolsystem.core.auth.logout.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.dasolsystem.core.jwt.repository.RedisJwtRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class logoutController {

    private final RedisJwtRepository redisJwtRepository;

    @PostMapping("/logout")
    public String logout(@RequestBody Long refreshTokenId){
        redisJwtRepository.deleteById(refreshTokenId);
        return "logout";
    }
}
