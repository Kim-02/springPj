package com.dasolsystem.core.auth.signup.dto;

import com.dasolsystem.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestSignupPostDto {

    @NotBlank(message = "아이디를 입력하세요")
    @Email(message = "이메일 형식을 확인하세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;

    @NotBlank(message = "유저 이름을 입력하세요")
    private String userName;

    @NotNull(message = "역할을 지정해주세요")
    private Role role;
}