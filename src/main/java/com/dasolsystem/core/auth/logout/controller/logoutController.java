package com.dasolsystem.core.auth.logout.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logout")
public class logoutController {

    @PostMapping("/api/vo/logout")
    public String logout(@RequestBody Long refreshTokenId){

        return "logout";
    }
}
