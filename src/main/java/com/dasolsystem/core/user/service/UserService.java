package com.dasolsystem.core.user.service;

import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserInfoDto getUserProfile(Member member);
    List<UserEventParticipationResponseDto> getUserEventParticipation(Member member);
    DepartmentDto setUserDepartment(String studentId, DepartmentDto department);
    String deleteUserDepartment(String studentId);
    void changeUserPermission(String requesterId, String reason, String targetId, String role);
    List<PermissionChangeLogDto> getPermissionChangeLog(String requesterId);
    void changeUserInfo(UserInfoDto userInfoDto);
    String userLeaveEvent(String studentId, Long eventId);
    byte[] buildResponseXlsx(MultipartFile file) throws IOException;
    UserYNResponseDto getUserYN(String studentId);
}
