package com.dasolsystem.core.deposit.repository;

import com.dasolsystem.core.entity.Deposit;
import com.dasolsystem.core.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUsersAndDepositTypeAndAmount(Users users, String depositType, Integer amount);

    @Query("SELECT d.users FROM Deposit d WHERE d.depositType = :depositType")
    List<Users> findUsersByDepositType(@Param("depositType") String depositType);
}
