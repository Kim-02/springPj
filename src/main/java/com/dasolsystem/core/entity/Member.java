package com.dasolsystem.core.entity;


import com.dasolsystem.config.BooleanToYNConverter;
import com.dasolsystem.core.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id",updatable = false,nullable = false)
    private Long id;


    @Column(name = "student_id", length = 10, nullable = false, unique = true)
    private String studentId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;       // 해시값 저장

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 1)
    private Gender gender;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "paid_user", columnDefinition = "ENUM('Y','N') DEFAULT 'N'", nullable = false,insertable=false)
    private Boolean paidUser;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_num", length = 20, nullable = false)
    private String phone;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code", nullable = false)
    private Role role;


}
