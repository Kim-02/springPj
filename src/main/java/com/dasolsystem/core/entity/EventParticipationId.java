package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
public class EventParticipationId {
    private static final long serialVersionUID = 1L;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "board_id", nullable = false)
    private Integer boardId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventParticipationId)) return false;
        EventParticipationId that = (EventParticipationId) o;
        return Objects.equals(memberId, that.memberId) &&
                Objects.equals(boardId,  that.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, boardId);
    }
}
