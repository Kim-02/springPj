package com.dasolsystem.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/dasol")
    public String index(){
        return "dasol Test";
    }

}
