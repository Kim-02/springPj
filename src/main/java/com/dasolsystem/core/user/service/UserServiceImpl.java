package com.dasolsystem.core.user.service;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.department.repository.DepartmentRepository;
import com.dasolsystem.core.entity.*;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.enums.Gender;
import com.dasolsystem.core.post.eventpost.repository.EventPostRepository;
import com.dasolsystem.core.post.repository.PostRepository;
import com.dasolsystem.core.user.dto.*;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import com.dasolsystem.core.user.repository.PaidUserRepository;
import com.dasolsystem.core.user.repository.PermissionChangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final EventPostRepository eventPostRepository;
    private final PaidUserRepository paidUserRepository;


    @Transactional
    public UserInfoDto getUserProfile(Member member) {
        return UserInfoDto.builder()
                .memberId(member.getMemberId())
                .studentId(member.getStudentId())
                .enterYear(member.getEnterYear())
                .gender(String.valueOf(member.getGender()))
                .email(member.getEmail())
                .phone(member.getPhone())
                .name(member.getName())
                .paidUser(member.getPaidUser())
                .roleName(member.getRole().getName())
                .department(member.getDepartment().getDepartmentRole())
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

    @Transactional(readOnly = true)
    public List<PermissionChangeLogDto> getPermissionChangeLog(String requesterId) {
        List<PermissionChange> changeList = permissionChangeRepository.findAllByIdRequesterId(Long.valueOf(requesterId));
        List<PermissionChangeLogDto> changeLogDtos = new ArrayList<>();
        for(PermissionChange permissionChange : changeList) {
            changeLogDtos.add(
                    PermissionChangeLogDto.builder()
                            .changeTime(permissionChange.getChangedAt())
                            .changeId(permissionChange.getTarget().getStudentId())
                            .changeName(permissionChange.getTarget().getName())
                            .changeLog(permissionChange.getReason())
                            .build()
            );
        }
        return changeLogDtos;
    }

    @Transactional
    public void changeUserInfo(UserInfoDto userInfoDto) {
        Member member = userRepository.findByStudentId(userInfoDto.getStudentId()).orElseThrow(
                ()-> new DBFaillException(ApiState.ERROR_500,"유저 정보를 찾을 수 없습니다.")
        );
        member.setEnterYear(userInfoDto.getEnterYear());
        member.setGender(Gender.valueOf(userInfoDto.getGender()));
        member.setName(userInfoDto.getName());
        member.setPhone(userInfoDto.getPhone());
        member.setEmail(userInfoDto.getEmail());
    }

    @Transactional
    public String userLeaveEvent(String studentId, Long eventId) {
        Member member = userRepository.findByStudentId(studentId).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"유저 정보를 찾을 수 없습니다.")
        );
        eventParticipationRepository.deleteByMemberMemberIdAndIdPostId(member.getMemberId(), eventId).orElseThrow(
                () -> new DBFaillException(ApiState.ERROR_500,"참여 정보를 찾을 수 없습니다.")
        );
        return eventPostRepository.findById(eventId).orElseThrow(()-> new DBFaillException(ApiState.ERROR_500,"이벤트 정보를 찾을 수 없습니다."))
                .getPost().getTitle();
    }


    @Transactional(readOnly = true)
    public byte[] buildResponseXlsx(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream();
             Workbook wb = WorkbookFactory.create(in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) { // 헤더 건너뜀
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Cell c0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL); // A열 학번
                if (c0 == null) continue;
                String studentId = fmt.formatCellValue(c0).trim();
                if (studentId.isEmpty()) continue;

                Cell c1 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 이름
                Cell c2 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 납부표시

                paidUserRepository.findByStudentId(studentId).ifPresentOrElse(
                        v -> { c1.setCellValue(v.getName()); c2.setCellValue("○"); },
                        () -> { c1.setCellValue("미납자");   c2.setCellValue("X"); }
                );
            }

            wb.write(baos);
            return baos.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public UserYNResponseDto getUserYN(String studentId) {
        UserYNResponseDto ynResponse = null;
        paidUserRepository.findByStudentId(studentId).ifPresentOrElse(
                v -> {
                     ynResponse.ok(v.getStudentId(),v.getName(),v.getCost());
                },
                ()-> {
                    ynResponse.error("미납자입니다.");
                }
        );
        return ynResponse;
    }
}
