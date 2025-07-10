package com.dasolsystem.core.auth.signup.dto;

import com.dasolsystem.core.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSignupDto {
    @NotNull
    @Size(min=10)
    private String student_id;

    @NotNull
    private String password;

    @NotNull
    private Gender gender;

    @NotNull
    @Size(max=50)
    private String email;

    @NotNull
    private String phone;

    @NotNull
    @Size(max=20)
    private String name;

}