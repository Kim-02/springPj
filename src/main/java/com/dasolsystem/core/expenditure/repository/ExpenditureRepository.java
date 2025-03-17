package com.dasolsystem.core.expenditure.repository;

import com.dasolsystem.core.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
    Optional<Expenditure> findByTransactionDateAndWithdrawalAmountAndContent(LocalDate transactionDate, Integer withdrawalAmount, String content);
}
