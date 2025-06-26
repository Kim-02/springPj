package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class PermissionChangeId {
    private static final long serialVersionUID = 1L;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof PermissionChangeId other)) return false;
        return Objects.equals(requesterId, other.requesterId) && Objects.equals(targetId, other.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requesterId, targetId);
    }
}
