package com.dasolsystem.core.upload.dto;

import com.dasolsystem.core.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 전체 회원 명단 엑셀용 DTO
 * 엑셀 헤더 순서: No., 학번, 이름, 성별, 휴대전화, 학적상태, 학과, 학년
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberExcelDto {
    private String studentId;   // 학번
    private String name;        // 이름
    private Gender gender;      // 성별
    private String phone;       // 휴대전화
    private String department;  // 학과
}
