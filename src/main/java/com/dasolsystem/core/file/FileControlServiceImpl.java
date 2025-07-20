package com.dasolsystem.core.file;

import com.dasolsystem.core.file.dto.FileUploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@Service
public class FileControlServiceImpl implements FileControlService {

    private final S3Client s3;
    private final String bucketName = "dasolfront-main";
    public FileControlServiceImpl(){
        this.s3 = S3Client.builder().region(Region.AP_NORTHEAST_2).build();
    }

    @Transactional
    public String uploadFile(FileUploadDto fileUploadDto) throws IOException {
        MultipartFile multipartFile = fileUploadDto.getFile();
        String key = fileUploadDto.getPath()+"/"+multipartFile.getOriginalFilename();


        File tempFile = File.createTempFile("upload-",multipartFile.getOriginalFilename());
        fileUploadDto.getFile().transferTo(tempFile);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(request, RequestBody.fromFile(tempFile));
        tempFile.delete();

        return key;
    }

    public byte[] getFileBytes(String key) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key).build();

        try(ResponseInputStream<GetObjectResponse> responseInputStream = s3.getObject(request)){
            return responseInputStream.readAllBytes();
        }
    }



}
