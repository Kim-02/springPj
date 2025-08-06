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

    /**
     * 입금자명(paymentName)을 기준으로 이벤트 참가 내역 전체를 조회하는 메서드.
     *
     * - 기존의 findByPaymentName(String)은 Optional<EventParticipation>로 반환되기 때문에
     *   동일한 paymentName을 가진 복수 사용자가 존재할 경우(예: 동명이인) 한 명만 조회됨.
     *
     * - "이영희"라는 이름으로 두 명이 paymentName을 설정했을 경우, 기존 메서드는 하나만 반환하여 위험.
     * - 이 메서드는 모두 조회하여 매칭되는 사람인 한명일 경우에만 안전하게 자동 입금 처리하게 한다.
     *
     * @param paymentName 입금자명 (엑셀에서 추출한 이름)
     * @return 해당 이름으로 등록된 모든 EventParticipation 목록
     */
    List<EventParticipation> findAllByPaymentName(String paymentName);


    boolean existsByPaymentName(String paymentName);
}
