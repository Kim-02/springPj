package com.dasolsystem.core.tests;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcTestController {

    @GetMapping("/test/signup")
    public String signup() {
        return "sign-up-page";
    }

    @GetMapping("/test/main")
    public String main() {
        return "main-page";
    }

    @GetMapping("/test/login")
    public String login() {
        return "login-page";
    }

    @GetMapping("/test/print")
    public String printing() {return "users-page";}

    @GetMapping("/deposit")
    public String deposit() {return "deposit-manage";}
}
