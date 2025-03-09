package com.dasolsystem.core.auth.user.controller;


import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.dto.*;
import com.dasolsystem.core.auth.user.service.UserService;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/personal_upload")
    public ResponseEntity<ResponseJson<?>> uploadPersonal(@RequestBody StudentSaveRequestDto studentSaveRequestDto) {
        try{
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(userService.savePersonalStudent(studentSaveRequestDto).getResult())
                            .build()
            );
        }catch(DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message(e.getMessage())
                            .build()
            );
        }

    }
    @GetMapping("/userdata")
    public ResponseEntity<ResponseJson<?>> getUserData(@RequestBody StudentSearchRequestDto requestDto) {
        try{
            StudentSearchResponseDto responseDto = userService.searchStudent(requestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(responseDto)
                            .build()
            );
        }catch(DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }
        

    }
    @DeleteMapping("/deleteuser")
    public ResponseEntity<ResponseJson<Object>> deleteUser(@RequestBody StudentSearchRequestDto requestDto) {
        try{
            String result = userService.deleteStudent(requestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(result)
                    .build()
            );
        }catch(DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }
    }
    @PostMapping("/updateuser")
    public ResponseEntity<ResponseJson<?>> updateUser(@RequestBody StudentUpdateRequestDto studentUpdateRequestDto) {
        try{
            String result = userService.updateStudentRoles(studentUpdateRequestDto.getEmailID(),studentUpdateRequestDto.getRole());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(result)
                            .build()
            );
        }catch(DBFaillException e){
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(e.getCode())
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }

    }
}
