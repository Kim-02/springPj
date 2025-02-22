package com.dasolsystem.core.entity;


import com.dasolsystem.core.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 20)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate createdAt;

    private Role access_role;

    //TODO 파일 첨부 기능은 나중에 구현





}
