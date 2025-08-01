package com.dasolsystem.core.upload.service;

import com.dasolsystem.core.auth.repository.RoleRepository;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.RoleCode;
import com.dasolsystem.core.enums.Gender;
import com.dasolsystem.core.upload.dto.MemberExcelDto;
import com.dasolsystem.core.upload.dto.PaymentExcelDto;
import com.dasolsystem.core.upload.parser.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UserRepository memberRepository; // Member = 사용자
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void processExcelFiles(MultipartFile memberFile, MultipartFile paymentFile) {
        List<MemberExcelDto> members = ExcelParser.parseMemberExcel(memberFile);
        List<PaymentExcelDto> payments = ExcelParser.parsePaymentExcel(paymentFile);

        // 납부자 이름 Set
        Set<String> paidNames = new HashSet<>();
        for (PaymentExcelDto dto : payments) {
            paidNames.add(dto.getName().trim());
        }

        RoleCode defaultRole = roleRepository.findByCode("USER")
                .orElseGet(() -> {
                    RoleCode newRole = new RoleCode("USER", "기본 사용자");
                    return roleRepository.save(newRole);
                });

        for (MemberExcelDto dto : members) {
            String studentId = dto.getStudentId();
            Optional<Member> existing = memberRepository.findByStudentId(studentId);

            if (existing.isEmpty()) {
                // 신규 등록
                String password = generateInitialPassword(studentId);
                Member newMember = Member.builder()
                        .studentId(studentId)
                        .name(dto.getName())
                        .phone(dto.getPhone() != null ? dto.getPhone() : "1111")
                        .gender(dto.getGender() != null ? dto.getGender() : Gender.M)
                        .enterYear(studentId.substring(0, 4))
                        .email(studentId + "@default.com")
                        .password(passwordEncoder.encode(password))
                        .role(defaultRole)
                        .department(null)
                        .paidUser(paidNames.contains(dto.getName()))
                        .build();

                memberRepository.save(newMember);
                log.info("✅ 신규 회원 등록: {}", dto.getName());
            } else {
                Member member = existing.get();
                if (paidNames.contains(member.getName()) && !Boolean.TRUE.equals(member.getPaidUser())) {
                    member.setPaidUser(true);
                    memberRepository.save(member);
                    log.info("💰 납부여부 업데이트: {}", member.getName());
                }
            }
        }

        // 납부자 중 명단에 없는 사람 자동 등록
        for (PaymentExcelDto dto : payments) {
            boolean exists = memberRepository.findByName(dto.getName()).stream().findFirst().isPresent();
            if (!exists) {
                String studentId = generateFakeStudentId();
                String password = generateInitialPassword(studentId);
                Member newMember = Member.builder()
                        .studentId(studentId)
                        .name(dto.getName())
                        .phone("1111")
                        .gender(Gender.M)
                        .enterYear(studentId.substring(0, 4))
                        .email(studentId + "@default.com")
                        .password(passwordEncoder.encode(password))
                        .role(defaultRole)
                        .department(null)
                        .paidUser(true)
                        .build();

                memberRepository.save(newMember);
                log.info("⚠️ 납부자 자동 회원 생성: {}", dto.getName());
            }
        }
    }

    private String generateInitialPassword(String studentId) {
        if (studentId.length() < 4) return "0000";
        return studentId.substring(0, 2) + studentId.substring(studentId.length() - 2);
    }

    private String generateFakeStudentId() {
        return "FAKE" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
