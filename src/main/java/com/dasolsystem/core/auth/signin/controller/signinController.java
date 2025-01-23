package com.dasolsystem.core.auth.signin.controller;

import com.dasolsystem.core.auth.signin.Dto.RequestSignincheckDto;
import com.dasolsystem.core.auth.signin.Dto.ResponseSignincheckDto;
import com.dasolsystem.core.auth.signin.service.signinService;
import com.dasolsystem.core.post.Dto.ResponseJson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class signinController {
    private final signinService service;
    @GetMapping("/cookie")
    public ResponseEntity<ResponseJson<Object>> getCookiePage(HttpServletResponse response) {
        var sessionExampleCookie = new Cookie("Session-Cookie-key", "Session-Cookie-value");
        sessionExampleCookie.setPath("/");
        sessionExampleCookie.setHttpOnly(false); // protect XSS attack
        sessionExampleCookie.setMaxAge(60 * 60);
        response.addCookie(sessionExampleCookie);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(sessionExampleCookie.getValue())
                        .build()
        );
    }

    @PostMapping("/session")
    public ResponseEntity<ResponseJson<Object>> sessionLogin(@RequestBody RequestSignincheckDto requestdto, HttpServletRequest request) {
        ResponseSignincheckDto responsedto = service.loginCheck(requestdto);
        if(responsedto.getState().value){
            HttpSession session = request.getSession();
            session.setAttribute("Session", requestdto);
        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(responsedto)
                        .build()
        );
    }

}
