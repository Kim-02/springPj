package com.dasolsystem.core.expenditure.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenditureResponseDto {
    private Integer expenditureAmount;
    private LocalDate expenditureDate;
    private String expenditureContent;
}
