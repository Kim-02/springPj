package com.dasolsystem.core.entity;


import com.dasolsystem.config.BooleanToYNConverter;
import com.dasolsystem.core.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String studentId;

    @Column(nullable = false)
    private String name;

    @Column(length = 50, unique = true)
    private String emailID;

    @Column(length = 100)
    private String password;

    @Column(length = 15)
    private String phone;

    @Column(length = 5)
    private String gender;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private Boolean paidUser;

    private String position;

    @JsonManagedReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Deposit> deposits = new ArrayList<>();




}
