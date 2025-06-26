package com.dasolsystem.core.guardian;

import com.dasolsystem.core.enums.JwtCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDto {
    private JwtCode jwtCode;
    private String accessToken;
    private String refreshToken;
    private String message;
}
