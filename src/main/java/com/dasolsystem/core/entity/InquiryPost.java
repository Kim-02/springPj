package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="inquiry_post")
public class InquiryPost {
    /**
     * 이 테이블의 PK이자, 게시글(Post) 테이블의 FK 역할도 합니다.
     */
    @Id
    @Column(name = "post_id", updatable = false, nullable = false)
    private Long postId;

    /**
     * 1:1 식별 관계 매핑.
     * 이 엔티티의 PK(postId)를 Post 엔티티의 PK와 공유하면서
     * FK 제약(게시글 참조)도 함께 설정
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 문의분류 코드 테이블(CodeInquiry)과 N:1 관계.
     * inquiry_code 칼럼을 FK로 사용해 CodeInquiry.code 를 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_code", nullable = false)
    private CodeInquiry inquiryCode;

}
