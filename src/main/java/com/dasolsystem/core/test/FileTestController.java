package com.dasolsystem.core.test;

import com.dasolsystem.core.guardian.SecurityGuardian;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class FileTestController {

    private final SecurityGuardian securityGuardian;

    @Transactional
    @PostMapping("/upload")
    public String upload(@RequestBody FileDto dto, HttpServletRequest servletRequest) throws IOException {
        String requestId = securityGuardian.getServletTokenClaims(servletRequest).getSubject();
        String bucketName = "testBucket";
        String key = requestId + "/" + dto.getFile().getOriginalFilename();

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .build(); // EC2 IAM Role 자동 사용

        File tempFile = File.createTempFile("upload-", dto.getFile().getOriginalFilename());
        dto.getFile().transferTo(tempFile);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromFile(tempFile));
        tempFile.delete(); // 임시파일 삭제

        return "✅ 업로드 성공: " + key;


    }
}
