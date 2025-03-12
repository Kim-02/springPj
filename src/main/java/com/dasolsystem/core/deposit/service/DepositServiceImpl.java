package com.dasolsystem.core.deposit.service;


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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        log.info("update deposit");
        List<Deposit> newDeposits = new ArrayList<>();
        List<Map<String, Integer>> noneFindUsers = new ArrayList<>();
        Map<String, List<String>> duplicateUsers = new HashMap<>();
        log.info(""+requestDto.getFile().getName());
        log.info(""+requestDto.getSelectAmount());
        try (Workbook workbook = new XSSFWorkbook(requestDto.getFile().getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            log.info("workbook sheet name: {}", sheet.getSheetName());
            log.info("workbook sheet number: {}", sheet.getPhysicalNumberOfRows());
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                Integer amount = (int) row.getCell(4).getNumericCellValue();
                String name = row.getCell(6).getStringCellValue();
                String depositType = requestDto.getDepositType();

                if (amount.equals(requestDto.getSelectAmount())) {
                    List<Users> usersList = userRepository.findByName(name);
                    if(usersList.size() == 1) {
                        // 단일 사용자 처리
                        Users user = usersList.get(0);
                        boolean exists = depositRepository.findByUsersAndDepositTypeAndAmount(user, depositType, amount).isPresent();
                        if(!exists){
                            if(Objects.equals(depositType, "학생회비") && !user.getPaidUser()){
                                user.setPaidUser(true);
                            }
                            Deposit deposit = Deposit.builder()
                                    .users(user)
                                    .depositType(depositType)
                                    .amount(amount)
                                    .depositedAt(LocalDateTime.now())
                                    .build();
                            log.info("save deposit: {}", deposit.getId());
                            newDeposits.add(deposit);
                        } else {
                            log.info("exist deposit: user={}, depositType={}, amount={}", user.getName(), depositType, amount);
                        }
                    } else if (usersList.size() > 1){ // 동명이인 처리
                        for(Users user : usersList){
                            duplicateUsers.computeIfAbsent(user.getName(), k -> new ArrayList<>()).add(user.getStudentId());
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
    public String updatePersonalDeposit(String studentId, String depositType, Integer amount) {
        // 사용자가 존재하는지 조회
        Users user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_502, "None find user"));

        // 동일한 depositType과 amount를 가진 입금 내역이 이미 있는지 확인
        Optional<Deposit> existingDeposit = depositRepository.findByUsersAndDepositTypeAndAmount(user, depositType, amount);
        if (existingDeposit.isPresent()) {
            // 이미 존재하는 경우, 해당 금액 정보를 반환
            return "exist amount: " + existingDeposit.get().getAmount();
        }

        // "학생회비" 항목의 경우, 미납 상태이면 paidUser를 true로 변경
        if (Objects.equals(depositType, "학생회비") && !user.getPaidUser()) {
            user.setPaidUser(true);
        }

        // 새로운 Deposit 객체 생성 및 저장
        Deposit deposit = Deposit.builder()
                .users(user)
                .depositType(depositType)
                .amount(amount)
                .depositedAt(LocalDateTime.now())
                .build();
        depositRepository.save(deposit);

        return "success "+deposit.getUsers().getName();
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
