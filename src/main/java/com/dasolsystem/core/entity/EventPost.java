    package com.dasolsystem.core.entity;

    import com.dasolsystem.config.BooleanToYNConverter;
    import jakarta.persistence.*;
    import lombok.*;

    @Entity
    @Table(name="event_post")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class EventPost {
        @Id
        @Column(name = "post_id", updatable = false, nullable = false)
        private Long postId;
        /**
         * 게시글(Post)와 1:1 식별 관계.
         * EventBoard.postId 를 PK 로 쓰면서
         * 동시에 Post 의 PK(postId)를 FK 로 참조
         */
        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @MapsId
        @JoinColumn(name = "post_id", nullable = false)
        private Post post;

        @Convert(converter = BooleanToYNConverter.class)
        @Column(name = "notice", columnDefinition = "ENUM('Y','N') DEFAULT 'N'", nullable = false)
        private Boolean notice;

        @Column(name = "pay_amount", nullable = false)
        private Integer payAmount;
    }
