package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name="approval_code")
public class ApprovalCode {
    @Id
    @Column(name="approval_code",length = 20)
    private String code;

    @Column(name="approval_name",length = 50,nullable = false)
    private String name;
}
