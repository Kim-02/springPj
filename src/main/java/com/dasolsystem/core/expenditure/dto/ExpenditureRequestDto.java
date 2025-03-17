package com.dasolsystem.core.expenditure.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenditureRequestDto {
    private MultipartFile multipartFile;
}
