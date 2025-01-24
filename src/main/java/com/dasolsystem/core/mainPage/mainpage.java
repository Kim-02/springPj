package com.dasolsystem.core.mainPage;

import com.dasolsystem.core.auth.Enum.JwtCode;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.post.Dto.ResponseJson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class mainpage {
    private JwtBuilder jwtBuilder;
    @GetMapping("/main")
    public ResponseEntity<ResponseJson<Object>> mainpage(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + token); // 디버깅용 출력
        JwtCode code = jwtBuilder.validateToken(token);
        System.out.println("JwtCode: " + code); // 결과 확인
        if (code == JwtCode.ACCESS) {
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("null")
                            .result("Welcome")
                            .build()
            );

        }
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("null")
                        .result("Error code: "+ code)
                        .build()
        );

    }
}
