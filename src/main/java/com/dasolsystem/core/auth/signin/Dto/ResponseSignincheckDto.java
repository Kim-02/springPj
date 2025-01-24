package com.dasolsystem.core.auth.signin.Dto;

import com.dasolsystem.core.auth.Enum.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSignincheckDto {
    private State state;
    private String name;
}
