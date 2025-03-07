package com.dasolsystem.core.auth.user.repository;

import com.dasolsystem.core.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    // 특정 금액을 납부한 사용자 조회
    List<Deposit> findByAmountIn(List<BigDecimal> amounts);
}