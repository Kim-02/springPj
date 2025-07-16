package com.dasolsystem.core.entity;


import com.dasolsystem.config.BooleanToYNConverter;
import com.dasolsystem.core.enums.Gender;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="member")
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler",
        "fieldHandler",
        "byteBuddyInterceptor"
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id",updatable = false,nullable = false)
    private Long memberId;

    /**
     * 본인참조
     */
    @ManyToMany
    @JoinTable(
            name = "member_relation",
            joinColumns = @JoinColumn(
                    name = "subordinate_id",
                    referencedColumnName = "member_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "manager_id",
                    referencedColumnName = "member_id"
            )
    )
    private List<Member> managers = new ArrayList<>();

    @ManyToMany(mappedBy = "managers")
    private List<Member> subordinates = new ArrayList<>();

    @Column(name = "student_id", length = 20, nullable = false, unique = true)
    private String studentId;

    @Column(name = "enter_year",length = 10, nullable = false)
    private String enterYear;

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

    @Column(name = "phone_num", length = 50, nullable = false)
    private String phone;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code", nullable = false)
    private RoleCode role;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "approvers")
    @JsonBackReference
    private List<ApprovalRequest> approvalRequests = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Member)) return false;
        return Objects.equals(memberId, ((Member)o).memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
