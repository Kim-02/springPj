package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "paid_users")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaidUsers {
    @Id
    private Long id;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "name")
    private String name;

    @Column(name = "cost")
    private int cost;
}
