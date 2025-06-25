package com.dasolsystem.core.amount.service;

import com.dasolsystem.core.amount.dto.AmountUsersResponseDto;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.file.dto.StudentIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmountServiceImpl implements AmountService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AmountUsersResponseDto> checkFeeStatus(List<StudentIdDto> studentIdsDtoList, List<String> depositNames) {
        List<AmountUsersResponseDto> result = new ArrayList<>();
        // 학번에 해당하는 사용자 목록 조회
        List<Member> users = userRepository.findAllByStudentIdIn(
                studentIdsDtoList.stream().map(StudentIdDto::getStudentId).collect(Collectors.toList())
        );
        for (Member user : users) {
            // 입금내역이 목록에 있는지 확인
            for (String depositName : depositNames) {
                boolean hasDeposit = user.getDeposits().stream()
                        .anyMatch(deposit -> deposit.getDepositType().equals(depositName));

                // 입금내역이 하나라도 있으면 추가
                if (hasDeposit) {
                    result.add(AmountUsersResponseDto.builder()
                            .studentId(user.getStudentId())
                            .name(user.getName())
                            .depositName(depositName)
                            .paidUser(user.getPaidUser() != null && user.getPaidUser() ? "O" : "X")
                            .build());
                }
            }
        }

        return result;
    }
}
