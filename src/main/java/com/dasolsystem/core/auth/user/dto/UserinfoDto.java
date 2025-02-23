package com.dasolsystem.core.auth.user.dto;


import com.dasolsystem.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserinfoDto {

    private String emailId;
    private String password;
    private String username;
    private Role role;
}
