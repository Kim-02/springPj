package com.dasolsystem.core.trasaction.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.EventParticipation;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.TransactionRecord;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.trasaction.dto.AmountResponseDto;
import com.dasolsystem.core.trasaction.dto.ErrorResponseDto;
import com.dasolsystem.core.trasaction.dto.ExpendTransactionDto;
import com.dasolsystem.core.trasaction.repository.TransactionRecordRepository;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {
    private final TransactionRecordRepository transactionRecordRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;

    /**
     * 이벤트에 해당하는 입금자명으로 입금을 할 시 해당 이벤트를 입금 처리를 하고
     * 만약 해당하는 이벤트가 없을 경우 이름을 이용해서 검색하고 해당 사용자가 가지고 있는 이벤트를 전부 반환한다.
     * 만약 이름을 DB에서 찾을 수 없다면 찾을 수 없는 이름으로 따로 반환한다.
     * @param file 입금 목록 xlsx파일
     * @return
     * 입금처리가 완료된 사용자 이름(completeUser)
     * 입금처리가 불가능한 사용자 이름(selectedUser) 내부에 해당 사용자가 가지고 있는 이벤트 목록(userEventList)
     * DB에서 찾을 수 없는 사용자 이름(noneFoundUsers)
     * 이렇게 Json으로 반환하면 된다.
     */
    @Transactional
    public ResponseJson<?> appendRecordSave(MultipartFile file) throws IOException {
        List<String> userFoundFail = new ArrayList<>();
        Map<String,List<String>> userDuplicate = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            for (int idx = 1; idx <= sheet.getLastRowNum(); idx++) {
                Row row = sheet.getRow(idx);
                if (row == null || formatter.formatCellValue(row.getCell(4))==null || formatter.formatCellValue(row.getCell(4)).isEmpty()) continue;
                //입금 로직
                String payDate = formatter.formatCellValue(row.getCell(2));
                String name = formatter.formatCellValue(row.getCell(6));
                Integer amount = Integer.valueOf(formatter.formatCellValue(row.getCell(5)));
                LocalDateTime date = LocalDateTime.parse(payDate, fmt);
                Optional<EventParticipation> optEp =
                        eventParticipationRepository.findByPaymentName(name);

                if (optEp.isPresent()) {
                    EventParticipation ep = optEp.get();
                    ep.setPaidAt(date);
                    ep.setPaymentStatus(true);
                    eventParticipationRepository.save(ep);

                    TransactionRecord transactionRecord = TransactionRecord.builder()
                            .amount(amount)
                            .txDate(date)
                            .member(ep.getMember())
                            .expense(false)
                            .build();
                    if(!transactionRecordRepository.existsByTxDate(date)){
                        transactionRecordRepository.save(transactionRecord);
                    }
                } else {
                    List<Member> foundUsers = userRepository.findByName(name);
                    if (foundUsers.isEmpty()) { //찾을 수 없다면 이름을 반환
                        userFoundFail.add(name);
                    } else if (foundUsers.size() > 1) { //두명 이상이라면 학번을 기록해서 반환
                        List<String> studentIds = foundUsers.stream()
                                .map(Member::getStudentId)
                                .collect(Collectors.toList());
                        userDuplicate.put(name, studentIds);
                    } else {//한 명밖에 없다면 기록
                        Member member = foundUsers.get(0);
                        TransactionRecord tr = TransactionRecord.builder()
                                .amount(amount)
                                .txDate(date)
                                .member(member)
                                .expense(false)
                                .build();
                        if(!transactionRecordRepository.existsByTxDate(date)){
                            transactionRecordRepository.save(tr);
                        }
                    }

                }
            }
            ErrorResponseDto erDto = ErrorResponseDto.builder()
                    .userDuplicate(userDuplicate)
                    .userFoundFail(userFoundFail)
                    .build();
            return ResponseJson.builder()
                    .status(200)
                    .message("이벤트 저장 및 입금 기록 완료")
                    .result(erDto)
                    .build();
        }catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Transactional
    public void expendRecordSave(ExpendTransactionDto dto){
        TransactionRecord record = TransactionRecord.builder()
                .txDate(LocalDateTime.now())
                .approvalRequest(dto.getApprovalRequest())
                .expense(true)
                .member(dto.getMember())
                .amount(dto.getAmount())
                .build();
        if(Objects.equals(dto.getCode(), "환불금")&& dto.getMember().getPaidUser()){
            Member member = dto.getMember();
            member.setPaidUser(false);
        }
        transactionRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public ResponseJson<?> getTotalAmount(){
        Integer nowAmount = transactionRecordRepository.findNowAmount();
        Integer expend = transactionRecordRepository.findNowExpendAmount();
        Integer append = transactionRecordRepository.findNowAppendAmount();
        AmountResponseDto amountResponseDto = AmountResponseDto.builder()
                .append(append)
                .expend(expend)
                .amount(nowAmount)
                .build();
        return ResponseJson.builder()
                .status(200)
                .message("totalAmount")
                .result(amountResponseDto).build();
    }
}
