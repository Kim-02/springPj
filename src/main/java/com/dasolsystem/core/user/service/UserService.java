package com.dasolsystem.core.user.service;

import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.user.dto.DepartmentDto;
import com.dasolsystem.core.user.dto.PermissionChangeLogDto;
import com.dasolsystem.core.user.dto.UserEventParticipationResponseDto;
import com.dasolsystem.core.user.dto.UserProfileResponseDto;

import java.util.List;

public interface UserService {
    UserProfileResponseDto getUserProfile(Member member);
    List<UserEventParticipationResponseDto> getUserEventParticipation(Member member);
    DepartmentDto setUserDepartment(String studentId, DepartmentDto department);
    String deleteUserDepartment(String studentId);
    void changeUserPermission(String requesterId, String reason, String targetId, String role);
    List<PermissionChangeLogDto> getPermissionChangeLog(String requesterId);
}
