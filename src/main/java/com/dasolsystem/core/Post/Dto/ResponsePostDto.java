package com.dasolsystem.core.Post.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePostDto {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdDate;
}
