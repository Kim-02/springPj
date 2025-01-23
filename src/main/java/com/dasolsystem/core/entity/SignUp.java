package com.dasolsystem.core.entity;

import jakarta.persistence.*;
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

    @Column(length = 10)
    private String password;

    @Column(length = 10)
    private String userName;

    @CreatedDate
    private LocalDateTime signInDate;


}
