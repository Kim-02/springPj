package com.dasolsystem.core.upload.dto;

import com.dasolsystem.core.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 전체 명단 엑셀용 DTO
 * 엑셀 헤더 순서: 이름, 학번, 전화번호, 성별
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberExcelDto {
    private String name;
    private String studentId;
    private String phone;
    private Gender gender;
}
