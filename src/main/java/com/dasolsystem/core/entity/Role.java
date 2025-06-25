package com.dasolsystem.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name="role_code")
public class Role {
    @Id
    @Column(name="role_code",length = 20)
    private String code;

    @Column(name="role_name",length = 50,nullable = false)
    private String name;


}
