package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "department")
public class Department {
    @Id
    @Column(name="department_role",nullable = false)
    private String departmentRole;
}
