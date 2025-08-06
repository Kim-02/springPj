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
        List<String> completeUser = new ArrayList<>(); // 처리 성공한 사용자 목록
        List<String> userFoundFail = new ArrayList<>(); // 이름으로도 찾지 못한 사용자
        Map<String, ErrorResponseDto.SelectedUserInfo> selectedUser = new HashMap<>(); // 동명이인 등 선택 필요한 사용자

        DataFormatter formatter = new DataFormatter();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 열 인덱스 맵 구성
            Map<String, Integer> columnIndex = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                String header = formatter.formatCellValue(cell).trim();
                columnIndex.put(header, cell.getColumnIndex());
            }

            // 필수 열 누락 시 예외
            if (!columnIndex.containsKey("거래일시") || !columnIndex.containsKey("입금액") || !columnIndex.containsKey("내용")) {
                throw new IOException("엑셀 파일에 '거래일시', '입금액', '내용' 열이 포함되어 있어야 합니다.");
            }

            // 각 행 처리
            for (int idx = 1; idx <= sheet.getLastRowNum(); idx++) {
                Row row = sheet.getRow(idx);
                if (row == null) continue;

                String payDate = formatter.formatCellValue(row.getCell(columnIndex.get("거래일시")));
                String amountStr = formatter.formatCellValue(row.getCell(columnIndex.get("입금액")));
                String content = formatter.formatCellValue(row.getCell(columnIndex.get("내용")));

                if (payDate == null || amountStr == null || content == null) continue;
                if (payDate.isBlank() || amountStr.isBlank() || content.isBlank()) continue;
                if (amountStr.equals("0")) continue;

                LocalDateTime date = LocalDateTime.parse(payDate, fmt);
                Integer amount = Integer.parseInt(amountStr);

                String paymentFull = content.trim();
                List<EventParticipation> matches = eventParticipationRepository.findAllByPaymentName(paymentFull);

                // 정확히 하나의 사용자가 매칭된 경우
                if (matches.size() == 1) {
                    EventParticipation ep = matches.get(0);

                    // completeUser에 추가 (입금 여부와 상관없이)
                    String eventTitle = ep.getPost().getTitle();
                    String name = ep.getMember().getName();
                    completeUser.add(eventTitle + name);

                    // 입금 상태가 false일 경우만 처리
                    if (!Boolean.TRUE.equals(ep.getPaymentStatus())) {
                        ep.setPaidAt(date);
                        ep.setPaymentStatus(true);
                        eventParticipationRepository.save(ep);

                        if (!transactionRecordRepository.existsByTxDate(date)) {
                            TransactionRecord record = TransactionRecord.builder()
                                    .amount(amount)
                                    .txDate(date)
                                    .member(ep.getMember())
                                    .expense(false)
                                    .build();
                            transactionRecordRepository.save(record);
                        }
                    }
                    continue;
                }

                // 복수 매칭된 경우 → 동명이인 처리
                if (matches.size() > 1) {
                    for (EventParticipation ep : matches) {
                        Member member = ep.getMember();
                        String key = ep.getPaymentName() + "(" + member.getStudentId() + ")";
                        List<String> titles = List.of(ep.getPost().getTitle());

                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder().events(new ArrayList<>(titles)).build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                    continue;
                }

                // 사용자 이름만 추출 후 Member 테이블에서 검색
                String nameOnly = content.replaceAll("[^가-힣]", "").trim();
                if (nameOnly.isBlank()) continue;

                List<Member> foundUsers = userRepository.findByName(nameOnly);

                if (foundUsers.isEmpty()) {
                    userFoundFail.add(nameOnly);
                } else if (foundUsers.size() > 1) {
                    // 이름만으로는 찾을 수 없는 경우 → 선택 사용자 목록에 추가
                    for (Member member : foundUsers) {
                        List<EventParticipation> events = eventParticipationRepository.findByMemberMemberId(member.getMemberId());
                        List<String> titles = events.stream().map(ep -> ep.getPost().getTitle()).toList();

                        String key = member.getName() + "(" + member.getStudentId() + ")";
                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder().events(new ArrayList<>(titles)).build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                } else {
                    // 한 명만 찾은 경우 → 해당 사용자의 모든 참여 이벤트 표시
                    Member member = foundUsers.get(0);
                    List<EventParticipation> events = eventParticipationRepository.findByMemberMemberId(member.getMemberId());
                    List<String> titles = events.stream().map(ep -> ep.getPost().getTitle()).toList();

                    if (!titles.isEmpty()) {
                        String key = member.getName();
                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder().events(new ArrayList<>(titles)).build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                }
            }

            // 결과 DTO 구성 후 응답
            ErrorResponseDto resultDto = ErrorResponseDto.builder()
                    .completeUser(completeUser)
                    .userFoundFail(userFoundFail)
                    .selectedUser(selectedUser)
                    .build();

            return ResponseJson.builder()
                    .status(200)
                    .message("이벤트 저장 및 입금 기록 완료")
                    .result(resultDto)
                    .build();

        } catch (Exception e) {
            throw new IOException("엑셀 파싱 또는 저장 중 오류 발생", e);
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
