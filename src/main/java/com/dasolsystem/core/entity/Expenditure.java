package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "expenditures")
public class Expenditure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래 일시 (날짜만 저장)
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    // 출금액
    @Column(name = "withdrawal_amount", nullable = false)
    private Integer withdrawalAmount;

    // 내용
    @Column(name = "content", length = 500)
    private String content;
}
