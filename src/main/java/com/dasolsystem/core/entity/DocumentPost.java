package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="document_post")
public class DocumentPost {
    @Id
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    /**
     * Post 엔티티와 1:1 식별 관계.
     * @MapsId 를 사용해 postId와 Post.postId를 공유
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Column(name = "real_location", length = 255, nullable = false)
    private String realLocation;
}
