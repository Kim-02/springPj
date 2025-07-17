package com.dasolsystem.core.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class FileTestController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        String bucketName = "your-bucket";
        String key = "uploads/" + file.getOriginalFilename();

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .build(); // EC2 IAM Role 자동 사용

        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(request, RequestBody.fromFile(tempFile));
        tempFile.delete(); // 임시파일 삭제

        return "✅ 업로드 성공: " + key;


    }
}
