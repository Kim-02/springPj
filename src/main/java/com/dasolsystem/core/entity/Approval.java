package com.dasolsystem.core.entity;

import com.dasolsystem.config.BooleanToYNConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime approvalDate;

    @Column(nullable = false)
    private String title;

    @Column(name = "drafter",nullable = false)
    private String drafterName;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "approval_user",  // 중간 테이블의 이름
            joinColumns = @JoinColumn(name = "approval_id"),  // 결재 테이블에서 참조하는 외래 키
            inverseJoinColumns = @JoinColumn(name = "user_id")  // 사용자 테이블에서 참조하는 외래 키
    )
    private List<Users> approvalUsers;

    @Column(nullable = false)
    private Integer deposit;

    @Column(name="accountNumber",nullable = false)
    private String accountNumber;

    @Column(nullable = false,columnDefinition = "CHAR(10) DEFAULT '학생회_흐름'")
    private String depositer;

    private String description;

    @Column(nullable = false,name="approvalCode")
    private String approvalCode;

    @Column(name = "receiptUrl") //영수증 처리를 위한 엔티티 필드
    private String receiptUrl;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private Boolean approved;
}
