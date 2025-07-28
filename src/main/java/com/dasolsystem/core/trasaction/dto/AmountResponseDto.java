package com.dasolsystem.core.trasaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmountResponseDto {
    private Integer amount;
    private Integer expend;
    private Integer append;
}
