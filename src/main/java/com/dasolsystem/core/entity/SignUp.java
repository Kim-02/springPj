package com.dasolsystem.core.entity;

import com.dasolsystem.core.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUp {
    @Id
    @Column(length = 20)
    private String emailID;

    @Column(length = 100)
    private String password;

    @Column(length = 10)
    private String userName;

    @CreatedDate
    private LocalDateTime signInDate;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

}
