package com.dasolsystem.core.Entity;

import com.dasolsystem.core.Post.Dto.RequestUpdatePostDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
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
