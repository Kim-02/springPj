package com.dasolsystem.core.approval.repository;

import com.dasolsystem.core.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
}
