package com.dasolsystem.core.approval.repository;

import com.dasolsystem.core.entity.ApprovalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    /**
     * 월별 범위(requestDate 기준) 페이징 조회
     */
    Page<ApprovalRequest> findByRequestDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
