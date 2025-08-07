package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /** 참여자 입금명*/
    @Column(name="payment_name",nullable = false)
    private String paymentName;

    /** 납부 여부 */
    @Column(name = "payment_status", nullable = false)
    private Boolean paymentStatus;

    /** 납부 일시 (nullable) */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** 물품 선택 **/
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "event_participation_item",
            joinColumns = {
                    @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
                    @JoinColumn(name = "post_id",   referencedColumnName = "post_id")
            },
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<EventItem> selectedItems;
}
