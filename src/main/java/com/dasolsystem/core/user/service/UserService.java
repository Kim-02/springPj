package com.dasolsystem.core.user.service;

import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.user.dto.*;

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
}
