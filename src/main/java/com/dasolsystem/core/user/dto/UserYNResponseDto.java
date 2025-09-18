package com.dasolsystem.core.user.dto;

import com.dasolsystem.core.enums.ApiState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record UserYNResponseDto(
        String studentId,
        String name,
        Integer cost,
        String errorMessage
) {
    public static UserYNResponseDto ok(String studentId, String name, int cost) {
        return new UserYNResponseDto(studentId, name, cost, null);
    }
    public static UserYNResponseDto error(String errorMessage) {
        return new UserYNResponseDto(null, null, null, errorMessage);
    }
}
