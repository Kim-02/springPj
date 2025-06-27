package com.dasolsystem.core.mainpage.controller;

import com.dasolsystem.core.handler.ResponseJson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainPageController {
    @Value("${jwt.secret.key}")
    private String SecretKey; //키값임.

    @GetMapping("/user_info")
    public ResponseEntity<ResponseJson<Object>> userInfo(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(
                                ResponseJson.builder()
                                        .status(401)
                                        .message("not availble token")
                                        .build()
                        );
            }
            String token = header.substring(7);
            byte[] KeyBytes = Decoders.BASE64.decode(SecretKey);
            Key key = Keys.hmacShaKeyFor(KeyBytes);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token).getBody();
            String username = claims.get("userName", String.class);
            String studentId = claims.getSubject();
            Map<String, String> data =
                    Map.of(
                            "name", username,
                            "studentId", studentId
                    );
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .result(data)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(
                            ResponseJson.builder()
                                    .status(401)
                                    .message("Error"+e.getMessage())
                            .build()
                    );
            }
    }


}
