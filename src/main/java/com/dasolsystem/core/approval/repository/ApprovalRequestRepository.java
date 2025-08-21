package com.dasolsystem.core.approval.repository;

import com.dasolsystem.core.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    /** 월별 범위(requestDate 기준) 전체 리스트 조회 */
    List<ApprovalRequest> findByRequestDateBetweenOrderByRequestDateDesc(
            LocalDateTime start, LocalDateTime end
    );
}
