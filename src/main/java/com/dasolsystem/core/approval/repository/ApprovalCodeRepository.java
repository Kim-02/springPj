package com.dasolsystem.core.approval.repository;

import com.dasolsystem.core.entity.ApprovalCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalCodeRepository extends JpaRepository<ApprovalCode, Long> {
    Optional<ApprovalCode> findByCode(String code);
}
