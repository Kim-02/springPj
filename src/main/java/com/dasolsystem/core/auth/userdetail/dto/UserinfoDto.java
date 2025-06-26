package com.dasolsystem.core.auth.userdetail.dto;


import com.dasolsystem.core.entity.Role;
import com.dasolsystem.core.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserinfoDto {
    private String studentId;
    private String name;
    private String email;
    private Gender gender;
    private String password;
    private Role role;
}
