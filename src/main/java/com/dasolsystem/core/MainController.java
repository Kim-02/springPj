package com.dasolsystem.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/dasol")
    @ResponseBody
    public String index(){
        return "dasol Test";
    }

}
