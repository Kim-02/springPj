package com.dasolsystem.core.auth.user.dto;

import com.dasolsystem.core.enums.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StudentUpdateRequestDto {
    private Role role;
    private String emailID;

}
