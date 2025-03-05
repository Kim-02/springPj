package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.core.auth.user.dto.StudentSaveResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    StudentSaveResponseDto saveStudent(MultipartFile file) throws IOException;
}
