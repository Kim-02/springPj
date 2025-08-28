package com.dasolsystem.core.approval.repository;

import com.dasolsystem.core.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    @Query(value = """
        select *
        from approval_request ar
        where ar.request_date >=:start and ar.request_date <:end
        """,nativeQuery = true)
    List<ApprovalRequest> findAllByRequestDateInMonthFetchApprovers(
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end
    );
}
