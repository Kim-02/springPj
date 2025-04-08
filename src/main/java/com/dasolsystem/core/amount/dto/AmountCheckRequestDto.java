package com.dasolsystem.core.amount.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountCheckRequestDto {
    private MultipartFile file;  // 엑셀 파일
    private List<String> depositNames;  // 입금내역 이름 (여러 개 가능)
}
