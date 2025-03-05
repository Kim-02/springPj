package com.dasolsystem.core.file.controller;

import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/excel")
@Slf4j
public class FileController {

//    @PostMapping("/upload")
//    public ResponseEntity<ResponseJson<?>> handleFileUpload(@RequestParam("file") MultipartFile file) {}
}
