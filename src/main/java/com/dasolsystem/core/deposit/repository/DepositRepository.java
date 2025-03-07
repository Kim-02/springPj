package com.dasolsystem.core.deposit.repository;

import com.dasolsystem.core.entity.Deposit;
import com.dasolsystem.core.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Optional<Deposit> findByUsersAndDepositTypeAndAmount(Users users, String depositType, Integer amount);
}
