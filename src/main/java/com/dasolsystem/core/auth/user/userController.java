package com.dasolsystem.core.auth.user;


import com.dasolsystem.core.post.Dto.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class userController {
//    @GetMapping("/{username}/userdata")
//    public ResponseEntity<ResponseJson<Object>> getUserData(@PathVariable String username) {
//
//    }
}
