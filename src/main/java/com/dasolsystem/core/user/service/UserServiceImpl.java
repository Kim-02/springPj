package com.dasolsystem.core.user.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.department.repository.DepartmentRepository;
import com.dasolsystem.core.entity.*;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.enums.Role;
import com.dasolsystem.core.user.dto.DepartmentDto;
import com.dasolsystem.core.user.dto.UserEventParticipationResponseDto;
import com.dasolsystem.core.user.dto.UserProfileResponseDto;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import com.dasolsystem.core.user.repository.PermissionChangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final DepartmentRepository departmentRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final PermissionChangeRepository permissionChangeRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public UserProfileResponseDto getUserProfile(Member member) {
        return UserProfileResponseDto.builder()
                .email(member.getEmail())
                .paidUser(member.getPaidUser())
                .phone(member.getPhone())
                .gender(String.valueOf(member.getGender()))
                .name(member.getName())
                .studentId(member.getStudentId())
                .build();
    }

    @Transactional
    public List<UserEventParticipationResponseDto> getUserEventParticipation(Member member) {
        List<UserEventParticipationResponseDto> responseDtos = new ArrayList<>();
        List<EventParticipation> events = eventParticipationRepository.findByMemberMemberId(member.getMemberId());
        for(EventParticipation eventParticipation : events) {
            responseDtos.add(
                    UserEventParticipationResponseDto.builder()
                            .postId(eventParticipation.getPost().getPostId())
                            .postTitle(eventParticipation.getPost().getTitle())
                            .postContent(eventParticipation.getPost().getContent())
                            .postTarget(eventParticipation.getPost().getTarget())
                            .postStartDate(eventParticipation.getPost().getStartDate())
                            .postEndDate(eventParticipation.getPost().getEndDate())
                            .eventPayAmount(eventParticipation.getPost().getEventPost().getPayAmount())
                            .eventPaidDate(eventParticipation.getPaidAt())
                            .eventPaidSuccess(eventParticipation.getPaymentStatus())
                            .build()
            );
        }
        return responseDtos;
    }

    @Transactional
    public DepartmentDto setUserDepartment(String studentId, DepartmentDto department) {
        Member member = userRepository.findByStudentId(studentId).orElseThrow(
                () -> new AuthFailException(ApiState.ERROR_700,"유저를 찾을 수 없습니다.")
        );
        member.setDepartment(departmentRepository.findById(department.getDepartmentName()).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"부서를 찾을 수 없습니다.")
        ));
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setResult(member.getDepartment().getDepartmentRole());
        return departmentDto;
    }

    @Transactional
    public String deleteUserDepartment(String studentId) {
        userRepository.findByStudentId(studentId).orElseThrow(
                ()-> new DBFaillException(ApiState.ERROR_500,"유저를 찾을 수 없습니다.")
        ).setDepartment(null);
        return "success";
    }

    @Transactional
    public void changeUserPermission(String requesterId, String reason, String targetId, String role){
        Member reqmember = userRepository.findByStudentId(requesterId).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"유저를 찾을 수 없습니다.")
        );
        Member tarmember = userRepository.findByStudentId(targetId).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"유저를 찾을 수 없습니다.")
        );
        RoleCode roleCode = roleRepository.findByCode(role).orElseThrow(
                ()-> new DBFaillException(ApiState.ERROR_500,"역할을 찾을 수 없습니다.")
        );
        tarmember.setRole(roleCode);
        PermissionChange permissionChange = PermissionChange.builder()
                .id(new PermissionChangeId())
                .reason(reason)
                .requester(reqmember)
                .target(tarmember)
                .changedAt(LocalDateTime.now())
                .build();
        permissionChangeRepository.save(permissionChange);
    }
}
