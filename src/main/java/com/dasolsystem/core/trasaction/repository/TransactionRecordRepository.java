package com.dasolsystem.core.trasaction.repository;

import com.dasolsystem.core.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    boolean existsByTxDate(LocalDateTime txDate);

    @Query(value = "SELECT COALESCE(SUM(tr.amount), 0) "
            + "FROM TransactionRecord tr "
            + "WHERE tr.is_expense = false",
    nativeQuery = true)
    Integer findNowAppendAmount();

    @Query(value = "SELECT COALESCE(SUM(tr.amount), 0) "
            + "FROM TransactionRecord tr "
            + "WHERE tr.is_expense = true",
            nativeQuery = true)
    Integer findNowExpendAmount();

    @Query(value = "SELECT SUM(CASE WHEN is_expense = false THEN amount ELSE -amount END)"+
            "AS nowamount"+
            "FROM transaction_record",
            nativeQuery = true)
    Integer findNowAmount();

}
