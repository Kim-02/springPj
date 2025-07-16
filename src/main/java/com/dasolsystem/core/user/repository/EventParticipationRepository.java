package com.dasolsystem.core.user.repository;

import com.dasolsystem.core.entity.EventParticipation;
import com.dasolsystem.core.entity.EventParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, EventParticipationId> {
    List<EventParticipation> findByMemberMemberId(Long memberId);
    Optional<EventParticipation>
    findByIdMemberIdAndIdPostId(
            Long memberId,
            Long postId
    );
    Optional<EventParticipation> findByPaymentName(String paymentName);
    boolean existsByPaymentName(String paymentName);
}
