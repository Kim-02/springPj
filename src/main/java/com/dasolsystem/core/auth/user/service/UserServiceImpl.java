package com.dasolsystem.core.auth.user.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.dto.StudentSaveRequestDto;
import com.dasolsystem.core.auth.user.dto.StudentSaveResponseDto;
import com.dasolsystem.core.auth.user.dto.StudentSearchRequestDto;
import com.dasolsystem.core.auth.user.dto.StudentSearchResponseDto;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.enums.ApiState;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public StudentSaveResponseDto saveStudent(MultipartFile file) throws IOException {
        List<Member> newUsers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 헤더 행을 건너뛰고 각 행을 처리
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String name = getCellValueAsString(row.getCell(0));
                String studentId = getCellValueAsString(row.getCell(1));
                // 2번 셀: 전화번호, 3번 셀: 성별
                String phone = getCellValueAsString(row.getCell(2));
                String gender = getCellValueAsString(row.getCell(3));

                // studentId가 없거나 이미 존재하는 경우 건너뛰기
                if (studentId == null || studentId.isEmpty()) {
                    continue;
                }
                if (userRepository.findByStudentId(studentId).isPresent()) {
                    continue;
                }

                Member user = Member.builder()
                        .name(name)
                        .role(Role.User)
                        .studentId(studentId)
                        .phone(phone)
                        .gender(gender)
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

    //변환 메서드
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

    @Transactional
    public StudentSaveResponseDto savePersonalStudent(StudentSaveRequestDto requestDto){
        userRepository.findByStudentIdAndName(requestDto.getStudentId(), requestDto.getName())
                .ifPresentOrElse(user -> {
                    // 이미 존재하는 경우 예외 발생
                    throw new DBFaillException(ApiState.ERROR_503, "Exist Users: "+user.getName());
                }, () -> {
                    // 존재하지 않으면 새 유저 생성 후 저장
                    Member newUser = Member.builder()
                            .name(requestDto.getName())
                            .studentId(requestDto.getStudentId())
                            .build();
                    userRepository.save(newUser);
                });

        return StudentSaveResponseDto.builder()
                .result("success save user "+requestDto.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public StudentSearchResponseDto searchStudent(StudentSearchRequestDto requestDto) {
        return userRepository.findByStudentIdAndName(requestDto.getStudentId(), requestDto.getName())
                .map(user -> StudentSearchResponseDto.builder()
                        .studentId(user.getStudentId())
                        .name(user.getName())
                        .deposits(user.getDeposits()) // 필요하면 deposit 목록도 반환 가능
                        .build())
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_502,"No such user"));
    }

    public String deleteStudent(StudentSearchRequestDto requestDto) {
            if(!userRepository.findByStudentId(requestDto.getStudentId()).isPresent()){
                throw new DBFaillException(ApiState.ERROR_502,"No such user");
            }
        userRepository.deleteByStudentId(requestDto.getStudentId());
        return "success delete user "+requestDto.getStudentId();
    }

    @Transactional
    @Description("API요청을 보내기 위한 메서드 외부사용 x")
    public String updateStudentRoles(String studentId, Role role) {
        int affectedRows = userRepository.updateUserRole(studentId,role);
        if(affectedRows == 0){
            throw new DBFaillException(ApiState.ERROR_502,"No such user");
        }
        return "User Role Update Success"+role;
    }

    @Transactional(readOnly = true)
    public String findStudentId(String studentName) {
        List<Member> userid = userRepository.findByName(studentName);
        //찾지 못하였다면
        if(userid.isEmpty()){
            return "none found";
        } else if (userid.size()>1) {//동명이인
            StringBuilder builder = new StringBuilder();
            for(Member user : userid){
                builder.append(user.getStudentId());
                builder.append(", ");
            }
            return builder.toString().trim();
        }
        else{
            return userid.get(0).getStudentId();
        }
    }
}
