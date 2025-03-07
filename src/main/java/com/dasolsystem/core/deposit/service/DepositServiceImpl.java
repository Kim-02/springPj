package com.dasolsystem.core.deposit.service;


import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.user.repository.UserRepository;
import com.dasolsystem.core.deposit.dto.DepositUsersRequestDto;
import com.dasolsystem.core.deposit.dto.DepositUsersResponseDto;
import com.dasolsystem.core.deposit.repository.DepositRepository;
import com.dasolsystem.core.entity.Deposit;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.ApiState;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositServiceImpl implements DepositService {
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;


    public DepositUsersResponseDto updateDeposit(DepositUsersRequestDto requestDto) throws IOException {
        List<Deposit> newDeposits = new ArrayList<>();
        List<Map<String, Integer>> noneFindUsers = new ArrayList<>();
        List<Map<String,String>> duplicateUsers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(requestDto.getFile().getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0){
                    continue;
                }
                Integer amount = (int) row.getCell(4).getNumericCellValue();
                String name = row.getCell(6).getStringCellValue();
                String depositType = requestDto.getDepositType();
                if(amount.equals(requestDto.getSelectAmount())){
                    List<Users> usersList = userRepository.findByName(name);
                    if(usersList.size()==1){
                        Users user = usersList.get(0);
                        boolean exists = depositRepository.findByUsersAndDepositTypeAndAmount(user, depositType, amount).isPresent();
                        if(!exists){
                            Deposit deposit = Deposit.builder()
                                    .users(user)
                                    .depositType(depositType)
                                    .amount(amount)
                                    .depositedAt(LocalDateTime.now())
                                    .build();
                            newDeposits.add(deposit);
                        }
                    }else if (usersList.size() > 1){ //동명 이인 처리를 위한 예외
                        for(Users user : usersList){
                            Map<String, String> map = new HashMap<>();
                            map.put(user.getName(),user.getStudentId());
                            duplicateUsers.add(map);
                        }
                    }else{
                        Map<String, Integer> userNotFoundMap = new HashMap<>();
                        userNotFoundMap.put(name,amount);
                        noneFindUsers.add(userNotFoundMap);
                    }
                }
            }
            depositRepository.saveAll(newDeposits);
            if(!noneFindUsers.isEmpty()){
                return DepositUsersResponseDto.builder()
                        .result("Not exist Users: "+Arrays.toString(noneFindUsers.toArray())+"\n"+
                                "DuplicateUsers: "+duplicateUsers)
                        .build();
            }
            return DepositUsersResponseDto.builder()
                    .result("success")
                    .build();
        }catch (Exception e){
            throw new DBFaillException(ApiState.ERROR_UNKNOWN,e.getMessage());
        }
    }
}
