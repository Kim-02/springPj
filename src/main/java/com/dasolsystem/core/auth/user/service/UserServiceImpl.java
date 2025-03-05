package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.dto.StudentSaveResponseDto;
import com.dasolsystem.core.auth.user.repository.DepositRepository;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.ApiState;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DepositRepository depositRepository;

    @Transactional
    public StudentSaveResponseDto saveStudent(MultipartFile file) throws IOException {
        List<Users> newUsers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 헤더 행을 건너뛰고 각 행을 처리
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String name = getCellValueAsString(row.getCell(0));
                String studentId = getCellValueAsString(row.getCell(1));

                // studentId가 없거나 이미 존재하는 경우 건너뛰기
                if (studentId == null || studentId.isEmpty()) {
                    continue;
                }
                if (userRepository.findByStudentId(studentId).isPresent()) {
                    continue;
                }

                Users user = Users.builder()
                        .name(name)
                        .studentId(studentId)
                        .build();
                newUsers.add(user);
            }
            userRepository.saveAll(newUsers);
        } catch (IOException e) {
            throw new DBFaillException(ApiState.ERROR_501, e.getMessage());
        }

        return StudentSaveResponseDto.builder()
                .result("success")
                .build();
    }

    /**
     * 셀의 값을 문자열로 안전하게 변환하는 헬퍼 메서드.
     * 숫자 타입은 BigDecimal을 사용해 과학적 표기법 없이 변환합니다.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                return bd.toPlainString();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
