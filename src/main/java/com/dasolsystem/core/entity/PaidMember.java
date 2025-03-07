package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaidMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paidId; // 기본키

    @OneToOne
    @JoinColumn(name = "users_id", nullable = false)
    private Users users; // 유저와 연결

    @Column(nullable = false)
    private Boolean paidMember = false; // 학생회비 납부 여부 (기본값 FALSE)
}
