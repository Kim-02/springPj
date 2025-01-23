package com.dasolsystem.core.entity;

import com.dasolsystem.core.post.Dto.RequestUpdatePostDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;


    public void update(RequestUpdatePostDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
}
