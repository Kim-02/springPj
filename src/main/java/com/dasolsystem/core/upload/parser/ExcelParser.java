package com.dasolsystem.core.upload.parser;

import com.dasolsystem.core.enums.Gender;
import com.dasolsystem.core.upload.dto.MemberExcelDto;
import com.dasolsystem.core.upload.dto.PaymentExcelDto;
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
     */
    public static List<MemberExcelDto> parseMemberExcel(MultipartFile file) {
        List<MemberExcelDto> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 0번째는 헤더
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getStringCell(row.getCell(0));
                String studentId = getStringCell(row.getCell(1));
                String phone = getStringCell(row.getCell(2));
                String genderStr = getStringCell(row.getCell(3));

                // 🚨 유효성 검사: 필수 정보가 없거나 학번이 너무 짧은 경우 무시
                if (name.isBlank() || studentId.isBlank() || studentId.length() < 4) {
                    log.warn("⚠️ 무시된 행 (줄 {}): name='{}', studentId='{}'", i + 1, name, studentId);
                    continue;
                }

                Gender gender = parseGender(genderStr);

                result.add(MemberExcelDto.builder()
                        .name(name)
                        .studentId(studentId)
                        .phone(phone)
                        .gender(gender)
                        .build());
            }
        } catch (Exception e) {
            log.error("❌ 회원 명단 엑셀 파싱 실패", e);
            throw new RuntimeException("회원 엑셀 파싱 중 오류 발생");
        }
        return result;
    }

    /**
     * 납부 명단 엑셀 파싱
     */
    public static List<PaymentExcelDto> parsePaymentExcel(MultipartFile file) {
        List<PaymentExcelDto> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 0번째는 헤더
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getStringCell(row.getCell(0));
                Cell amountCell = row.getCell(1);

                // 🚨 이름이 없거나 납부 금액이 숫자가 아닐 경우 무시
                if (name.isBlank() || amountCell == null || amountCell.getCellType() != CellType.NUMERIC) {
                    log.warn("⚠️ 무시된 납부자 행 (줄 {}): name='{}'", i + 1, name);
                    continue;
                }

                int amount = (int) amountCell.getNumericCellValue();

                result.add(PaymentExcelDto.builder()
                        .name(name)
                        .amount(amount)
                        .build());
            }
        } catch (Exception e) {
            log.error("❌ 납부 명단 엑셀 파싱 실패", e);
            throw new RuntimeException("납부 엑셀 파싱 중 오류 발생");
        }
        return result;
    }

    /**
     * 셀 값을 문자열로 변환
     */
    private static String getStringCell(Cell cell) {
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }

    /**
     * 성별 문자열을 Gender enum으로 변환
     */
    private static Gender parseGender(String value) {
        if (value == null) return Gender.M; // 기본값
        if (value.equalsIgnoreCase("여") || value.equalsIgnoreCase("F")) return Gender.F;
        return Gender.M;
    }
}
