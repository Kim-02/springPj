package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="inquiry_code")
public class CodeInquiry {
    @Id
    @Column(name="inquiry_code",length = 20)
    private String code;

    @Column(name="inquiry_name",length = 50,nullable = false)
    private String name;
}
