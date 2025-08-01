package com.dasolsystem.core.upload.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.upload.dto.PaymentStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCheckServiceImpl implements PaymentCheckService {

    private final UserRepository userRepository;

    @Override
    public PaymentStatusDto checkPaymentStatus(String name, String studentId) {
        if ((name == null || name.isBlank()) && (studentId == null || studentId.isBlank())) {
            throw new IllegalArgumentException("이름 또는 학번 중 하나는 필수입니다.");
        }

        if (name != null && !name.isBlank() && studentId != null && !studentId.isBlank()) {
            throw new IllegalArgumentException("이름과 학번 중 하나만 입력하세요.");
        }

        Member member = null;

        if (studentId != null && !studentId.isBlank()) {
            member = userRepository.findByStudentId(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 학번의 회원이 존재하지 않습니다."));
        } else {
            member = userRepository.findByName(name).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 이름의 회원이 존재하지 않습니다."));
        }

        return new PaymentStatusDto(member.getName(), member.getStudentId(), member.getPaidUser());
    }
}
