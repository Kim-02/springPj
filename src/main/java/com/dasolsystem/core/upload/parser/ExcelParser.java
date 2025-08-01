package com.dasolsystem.core.upload.parser;

import com.dasolsystem.core.enums.Gender;
import com.dasolsystem.core.upload.dto.MemberExcelDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelParser {

    private static final DataFormatter formatter = new DataFormatter();

    /**
     * 전체 회원 명단 엑셀 파싱
     * 엑셀 헤더 순서: No., 학번, 이름, 성별, 휴대전화, 학적상태, 학과, 학년
     */
    public static List<MemberExcelDto> parseMemberExcel(MultipartFile file) {
        List<MemberExcelDto> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 0번째는 헤더
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentId = getStringCell(row.getCell(1));
                String name = getStringCell(row.getCell(2));
                String genderStr = getStringCell(row.getCell(3));
                String phone = getStringCell(row.getCell(4));
                String department = getStringCell(row.getCell(6));

                // 유효성 검사
                if (studentId.isBlank() || studentId.length() < 4 || name.isBlank()) {
                    log.warn("⚠️ 무시된 행 (줄 {}): name='{}', studentId='{}'", i + 1, name, studentId);
                    continue;
                }

                Gender gender = parseGender(genderStr);

                result.add(MemberExcelDto.builder()
                        .studentId(studentId)
                        .name(name)
                        .phone(phone)
                        .gender(gender)
                        .department(department)
                        .build());
            }
        } catch (Exception e) {
            log.error("❌ 회원 명단 엑셀 파싱 실패", e);
            throw new RuntimeException("회원 엑셀 파싱 중 오류 발생");
        }
        return result;
    }

    private static String getStringCell(Cell cell) {
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    private static Gender parseGender(String value) {
        if (value == null) return Gender.M;
        if (value.equalsIgnoreCase("여") || value.equalsIgnoreCase("F") || value.equalsIgnoreCase("female")) return Gender.F;
        return Gender.M;
    }
}
