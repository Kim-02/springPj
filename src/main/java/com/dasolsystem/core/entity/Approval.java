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
    @JoinColumn(name = "users_id",nullable = false)
    @Column(name="approvaluser")
    private List<Users> approvalUsers;

    @Column(nullable = false)
    private Integer deposit;

    @Column(name="accountnumber",nullable = false)
    private String accountNumber;

    @Column(nullable = false,columnDefinition = "CHAR(10) DEFAULT '학생회_흐름'")
    private String depositer;

    private String description;

    @Column(nullable = false,name="approvalcode")
    private String approvalCode;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private Boolean approved;
}
