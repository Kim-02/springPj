package com.dasolsystem.core.file.service;

import com.dasolsystem.core.amount.dto.AmountUsersResponseDto;
import com.dasolsystem.core.deposit.dto.DepositUsersDto;
import com.dasolsystem.core.file.dto.StudentIdDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ExcelService {

    public List<StudentIdDto> extractStudentIdsFromExcel(MultipartFile file) throws IOException {
        List<StudentIdDto> studentIdsDtoList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        log.info("file: {}", file.getOriginalFilename());
        Sheet sheet = workbook.getSheetAt(0);  // 첫 번째 시트

        // 엑셀 파일에서 학번이 있는 1열을 읽어서 DTO로 변환
        for (Row row : sheet) {
            // 첫 번째 행은 헤더일 수 있으므로 건너뛰기 (row.getRowNum() != 0)
            if (row.getRowNum() == 0) continue;  // 첫 번째 행 건너뛰기 (헤더)

            // 첫 번째 열이 학번이라고 가정
            Cell cell = row.getCell(0);
            String studentId = getCellValueAsString(cell);  // 셀 값을 문자열로 변환

            if (studentId != null && !studentId.isEmpty()) {
                studentIdsDtoList.add(new StudentIdDto(studentId));
            }
        }
        workbook.close();
        return studentIdsDtoList;
    }
    public ByteArrayOutputStream generateResultExcel(List<AmountUsersResponseDto> results) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");

        // 헤더 행 생성
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("학번");
        header.createCell(1).setCellValue("이름");
        header.createCell(2).setCellValue("입금내역이름");
        header.createCell(3).setCellValue("학생회비 납부 여부");

        // 데이터 행 생성
        int rowNum = 1;
        for (AmountUsersResponseDto result : results) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.getStudentId());
            row.createCell(1).setCellValue(result.getName());
            row.createCell(2).setCellValue(result.getDepositName());
            row.createCell(3).setCellValue(result.getPaidUser());
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 숫자 타입을 문자열로 변환
                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                return bd.toPlainString();
            case BOOLEAN:
                // 불리언 타입을 문자열로 변환
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 수식일 경우 수식의 문자열로 변환
                return cell.getCellFormula();
            default:
                return ""; // 알 수 없는 형식은 빈 문자열로 처리
        }
    }
}