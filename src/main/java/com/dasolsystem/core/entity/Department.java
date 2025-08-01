package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * 학과 정보 엔티티
 * - PK: departmentRole (ex. "컴퓨터공학부")
 * - 자동 생성 및 조회 가능하게 builder/생성자 포함
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Builder
@Entity
@Table(name = "department")
public class Department {

    @Id
    @Column(name = "department_role", nullable = false)
    private String departmentRole;
}
