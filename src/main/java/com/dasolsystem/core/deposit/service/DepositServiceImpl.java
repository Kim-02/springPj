package com.dasolsystem.core.deposit.service;


import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.deposit.dto.DepositResultDto;
import com.dasolsystem.core.deposit.dto.DepositUsersDto;
import com.dasolsystem.core.deposit.dto.DepositUsersRequestDto;
import com.dasolsystem.core.deposit.dto.DepositUsersResponseDto;
import com.dasolsystem.core.deposit.repository.DepositRepository;
import com.dasolsystem.core.entity.Deposit;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.ApiState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.HaltonSequenceGenerator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositServiceImpl implements DepositService {
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepositUsersResponseDto<Object> updateDeposit(DepositUsersRequestDto requestDto) throws IOException {
        List<Deposit> newDeposits = new ArrayList<>();
        List<Map<String, Integer>> noneFindUsers = new ArrayList<>();
        Map<String, String> duplicateUsers = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(requestDto.getFile().getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                Integer amount = (int) row.getCell(4).getNumericCellValue();
                String name = row.getCell(6).getStringCellValue();
                String depositType = requestDto.getDepositType();

                if (amount.equals(requestDto.getSelectAmount())) {
                    List<Users> usersList = userRepository.findByName(name);
                    if (usersList.size() == 1) {
                        Users user = usersList.get(0);
                        boolean exists = depositRepository.findByUsersAndDepositTypeAndAmount(user, depositType, amount).isPresent();
                        if (!exists) {
                            if (Objects.equals(depositType, "학생회비") && !user.getPaidUser()) {
                                user.setPaidUser(true);
                            }
                            Deposit deposit = Deposit.builder()
                                    .users(user)
                                    .depositType(depositType)
                                    .amount(amount)
                                    .depositedAt(LocalDateTime.now())
                                    .build();
                            newDeposits.add(deposit);
                        } else {
                            // 존재하는 deposit인 경우 로그에 기록
                            log.info("exist deposit: user={}, depositType={}, amount={}", user.getName(), depositType, amount);
                        }
                    } else if (usersList.size() > 1) { // 동명이인 처리 예외
                        for (Users user : usersList) {
                            duplicateUsers.put(user.getName(), user.getStudentId());
                        }
                    } else {
                        Map<String, Integer> userNotFoundMap = new HashMap<>();
                        userNotFoundMap.put(name, amount);
                        noneFindUsers.add(userNotFoundMap);
                    }
                }
            }
            depositRepository.saveAll(newDeposits);
            if (!noneFindUsers.isEmpty()) {
                return DepositUsersResponseDto.builder()
                        .result(DepositResultDto.builder()
                                .noneFinds(Arrays.toString(noneFindUsers.toArray()))
                                .duplicated(duplicateUsers.toString())
                                .build()
                        ).build();
            }
            return DepositUsersResponseDto.builder()
                    .result("success")
                    .build();
        } catch (Exception e) {
            throw new DBFaillException(ApiState.ERROR_UNKNOWN, e.getMessage());
        }
    }
    @Transactional
    public String updatePersonalDeposit(String studentId, String depositType, Integer amount ){
        userRepository.findByStudentId(studentId).ifPresentOrElse(
                users -> {
                    if(Objects.equals(depositType, "학생회비") && !users.getPaidUser()){
                        users.setPaidUser(true);
                    }
                    Deposit deposit = Deposit.builder()
                            .users(users)
                            .depositType(depositType)
                            .amount(amount)
                            .depositedAt(LocalDateTime.now())
                            .build();
                    depositRepository.save(deposit);
                },
                () -> {
                    throw new DBFaillException(ApiState.ERROR_502, "None fine user");
                }
        );
        return "success";
    }
    @Transactional(readOnly = true)
    public List<DepositUsersDto> findDepositUsers(String depositType) {
        List<Users> usersList = depositRepository.findUsersByDepositType(depositType);

        return usersList.stream()
                .flatMap(user -> user.getDeposits().stream()
                        .filter(deposit -> deposit.getDepositType().equals(depositType))
                        .map(deposit -> DepositUsersDto.builder()
                                .name(user.getName())
                                .studentId(user.getStudentId())
                                .paidUser(user.getPaidUser() != null && user.getPaidUser() ? "Y" : "N")
                                .amount(deposit.getAmount())
                                .build()))
                .collect(Collectors.toList());
    }

    public ByteArrayOutputStream generateExcelFile(List<DepositUsersDto> depositUsers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users Deposit Data");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("이름");
        headerRow.createCell(1).setCellValue("학번");
        headerRow.createCell(2).setCellValue("학회비 납부 여부");
        headerRow.createCell(3).setCellValue("납부 금액");

        // 데이터 추가
        int rowNum = 1;
        for (DepositUsersDto user : depositUsers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getName());
            row.createCell(1).setCellValue(user.getStudentId());
            row.createCell(2).setCellValue(user.getPaidUser());
            row.createCell(3).setCellValue(user.getAmount());
        }

        // ByteArrayOutputStream으로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

}
