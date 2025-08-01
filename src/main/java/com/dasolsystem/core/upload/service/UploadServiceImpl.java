package com.dasolsystem.core.upload.service;

import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.department.repository.DepartmentRepository;
import com.dasolsystem.core.entity.Department;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.RoleCode;
import com.dasolsystem.core.enums.Gender;
import com.dasolsystem.core.upload.dto.MemberExcelDto;
import com.dasolsystem.core.upload.parser.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UserRepository memberRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 엑셀로부터 회원 정보를 읽고 DB에 저장 (납부여부는 모두 false)
     */
    @Override
    public void processExcelFile(MultipartFile memberFile) {
        List<MemberExcelDto> members = ExcelParser.parseMemberExcel(memberFile);

        // 기본 권한 USER 로드 (없으면 자동 생성)
        RoleCode defaultRole = roleRepository.findByCode("USER")
                .orElseGet(() -> {
                    RoleCode newRole = new RoleCode("USER", "기본 사용자");
                    return roleRepository.save(newRole);
                });

        for (MemberExcelDto dto : members) {
            String studentId = dto.getStudentId();

            // 이미 존재하는 학번이면 건너뜀
            Optional<Member> existing = memberRepository.findByStudentId(studentId);
            if (existing.isPresent()) {
                log.info("⚠️ 이미 존재하는 학번: {} → 건너뜀", studentId);
                continue;
            }

            // 학과명 → Department Entity 조회 or 자동 생성
            String deptName = dto.getDepartment();
            Department department = departmentRepository.findByDepartmentRole(deptName)
                    .orElseGet(() -> {
                        Department newDept = Department.builder()
                                .departmentRole(deptName)
                                .build();
                        log.info("📌 새 학과 자동 생성: {}", deptName);
                        return departmentRepository.save(newDept);
                    });

            // 초기 비밀번호 생성
            String password = generateInitialPassword(studentId);

            // 회원 생성 및 저장
            Member newMember = Member.builder()
                    .studentId(studentId)
                    .name(dto.getName())
                    .phone(dto.getPhone() != null ? dto.getPhone() : "1111")
                    .gender(dto.getGender() != null ? dto.getGender() : Gender.M)
                    .enterYear(studentId.substring(0, 4))
                    .email(studentId + "@koreatech.ac.kr")
                    .password(passwordEncoder.encode(password))
                    .role(defaultRole)
                    .department(department)
                    .paidUser(false)
                    .build();

            memberRepository.save(newMember);
            log.info("✅ 신규 회원 등록: {} ({})", newMember.getName(), newMember.getStudentId());
        }
    }

    /**
     * 초기 비밀번호 생성: 학번 앞 2자리 + 뒤 2자리
     */
    private String generateInitialPassword(String studentId) {
        if (studentId.length() < 4) return "0000";
        return studentId.substring(0, 2) + studentId.substring(studentId.length() - 2);
    }
}
