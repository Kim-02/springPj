package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="permission_change")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionChange {
    // 복합키 선언
    @EmbeddedId
    private PermissionChangeId id;

    // PK(requester_id)와 FK(Member) 매핑
    @MapsId("requesterId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    // PK(target_id)와 FK(Member) 매핑
    @MapsId("targetId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private Member target;

    @Column(name = "reason", length = 255)
    private String reason;
}
