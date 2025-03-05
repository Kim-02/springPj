package com.dasolsystem.core.auth.user.controller;


import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.dto.StudentSaveResponseDto;
import com.dasolsystem.core.auth.user.service.UserService;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseJson<?>> upload(@RequestParam("file") MultipartFile file) {
        try{
            StudentSaveResponseDto responseDto = userService.saveStudent(file);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .result(responseDto.getResult())
                            .message("success")
                            .build()
            );
        }catch (DBFaillException | IOException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(501)
                            .message("failed to upload file")
                            .result(e.getMessage())
                            .build()
            );
        }
    }
}
