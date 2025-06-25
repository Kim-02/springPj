package com.dasolsystem.core.deposit.repository;

import com.dasolsystem.core.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUsersAndDepositTypeAndAmount(Member users, String depositType, Integer amount);

    @Query("SELECT d.users FROM Deposit d WHERE d.depositType = :depositType")
    List<Member> findUsersByDepositType(@Param("depositType") String depositType);
}
