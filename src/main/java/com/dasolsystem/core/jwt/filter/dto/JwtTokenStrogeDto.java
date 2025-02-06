package com.dasolsystem.core.jwt.filter.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenStrogeDto {
    private String refreshTokenId;
    private String accessToken;
}
