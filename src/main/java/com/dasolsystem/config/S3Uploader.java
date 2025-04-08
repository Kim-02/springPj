//package com.dasolsystem.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class S3Uploader {
//
//    private final S3Client s3Client;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    @Value("${cloud.aws.region.static}")
//    private String region; // 👈 여기에 리전 주입
//
//    public String upload(MultipartFile file, String dirName) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalFilename;
//
//        PutObjectRequest putRequest = PutObjectRequest.builder()
//                .bucket(bucket)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//
//        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//
//        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName); // ✅ region 직접 사용
//    }
//}