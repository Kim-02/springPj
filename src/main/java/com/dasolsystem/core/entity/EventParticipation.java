package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="event_participation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Access(AccessType.FIELD)
public class EventParticipation {
    @EmbeddedId
    private EventParticipationId id;

    /** 연관 회원 */
    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** 연관 게시글 */
    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** 납부 여부 */
    @Column(name = "payment_status", nullable = false)
    private Boolean paymentStatus;

    /** 납부 일시 (nullable) */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
