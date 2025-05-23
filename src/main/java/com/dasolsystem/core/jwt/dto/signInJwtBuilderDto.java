package com.dasolsystem.core.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class signInJwtBuilderDto {
    private String userName;
    private String rtoken;
}
