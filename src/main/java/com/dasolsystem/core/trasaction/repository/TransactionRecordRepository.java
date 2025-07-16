package com.dasolsystem.core.trasaction.repository;

import com.dasolsystem.core.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    boolean existsByTxDate(LocalDateTime txDate);
}
