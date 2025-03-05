package com.dasolsystem.core.auth.user.repository;

import com.dasolsystem.core.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
}
