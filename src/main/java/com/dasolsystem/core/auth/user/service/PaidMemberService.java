package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.core.entity.Deposit;
import com.dasolsystem.core.entity.PaidMember;
import com.dasolsystem.core.auth.user.repository.DepositRepository;
import com.dasolsystem.core.auth.user.repository.PaidMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaidMemberService {

    private final DepositRepository depositRepository;
    private final PaidMemberRepository paidMemberRepository;

    @Transactional
    public void updatePaidMembers() {
        // 280000원 또는 300000원을 납부한 사용자 조회
        List<Deposit> eligibleDeposits = depositRepository.findByAmountIn(Arrays.asList(
                BigDecimal.valueOf(280000), BigDecimal.valueOf(300000)
        ));

        // PaidMember 엔티티 업데이트
        for (Deposit deposit : eligibleDeposits) {
            PaidMember paidMember = PaidMember.builder()
                    .users(deposit.getUsers()) // 동일한 사용자 연결
                    .paidMember(true) // 납부 완료
                    .build();
            paidMemberRepository.save(paidMember);
        }
    }
}