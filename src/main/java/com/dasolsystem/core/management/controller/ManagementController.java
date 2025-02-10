package com.dasolsystem.core.management.controller;


import com.dasolsystem.core.management.service.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ManagementController {
    private final ManagementService managementService;
    @GetMapping("/print")
    public String print(){
        managementService.printAllCourseWithUsers();
        return "get logs";
    }
}
