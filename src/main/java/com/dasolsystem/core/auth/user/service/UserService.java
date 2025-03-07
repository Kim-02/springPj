package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.core.auth.user.dto.StudentSaveRequestDto;
import com.dasolsystem.core.auth.user.dto.StudentSaveResponseDto;
import com.dasolsystem.core.auth.user.dto.StudentSearchRequestDto;
import com.dasolsystem.core.auth.user.dto.StudentSearchResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    StudentSaveResponseDto saveStudent(MultipartFile file) throws IOException;
    StudentSaveResponseDto savePersonalStudent(StudentSaveRequestDto requestDto);
    StudentSearchResponseDto searchStudent(StudentSearchRequestDto requestDto);
    String deleteStudent(StudentSearchRequestDto requestDto);
}
