package com.dasolsystem.core.trasaction.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.EventParticipation;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.TransactionRecord;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.trasaction.dto.AmountResponseDto;
import com.dasolsystem.core.trasaction.dto.EventDto;
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



    /**
     * [엑셀 파일을 기반으로 입금 내역 처리 및 결과 반환 메서드]
     *
     * 주요 동작 흐름:
     * 1. 엑셀 파일로부터 거래 일시, 입금액, 내용 열을 파싱
     * 2. '내용' 값을 payment_name으로 간주하여 이벤트 참여자 목록에서 정확히 일치하는 사용자를 검색
     *   - 단일 매칭: 바로 입금 처리 및 거래 기록 저장
     *   - 복수 매칭: 사용자 선택 유도 정보 생성
     *   - 매칭 실패: 이름만 추출해 fallback 검색 수행
     * 3. 최종적으로 입금 완료된 사용자 목록, 찾지 못한 사용자 목록, 선택 유도 정보 Map을 포함한 DTO 반환
     */
    @Transactional
    public ResponseJson<?> appendRecordSave(MultipartFile file) throws IOException {
        // ✅ 입금 처리가 완료된 사용자의 이름(이벤트명+이름)을 저장
        List<String> completeUser = new ArrayList<>();

        // ✅ fallback 이름 검색 시에도 매칭되지 않은 이름만 따로 저장
        List<String> userFoundFail = new ArrayList<>();

        // ✅ 중복 사용자에 대해 선택 유도할 정보를 저장하는 맵 (key: 이름(학번), value: 이벤트 목록 및 학번)
        Map<String, ErrorResponseDto.SelectedUserInfo> selectedUser = new HashMap<>();

        // 셀의 값을 문자열로 읽기 위한 포맷터
        DataFormatter formatter = new DataFormatter();
        // 엑셀에 저장된 '거래일시'를 LocalDateTime으로 파싱하기 위한 포맷
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // 첫 번째 시트를 기준으로 작업
            Sheet sheet = workbook.getSheetAt(0);

            // ✅ 엑셀의 헤더(첫 줄)에서 필요한 열들의 인덱스를 파악
            Map<String, Integer> columnIndex = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                String header = formatter.formatCellValue(cell).trim();
                columnIndex.put(header, cell.getColumnIndex());
            }

            // 필수 열이 존재하는지 확인
            if (!columnIndex.containsKey("거래일시") || !columnIndex.containsKey("입금액") || !columnIndex.containsKey("내용")) {
                throw new IOException("엑셀 파일에 '거래일시', '입금액', '내용' 열이 포함되어 있어야 합니다.");
            }

            // ✅ 데이터 행들을 반복 처리
            for (int idx = 1; idx <= sheet.getLastRowNum(); idx++) {
                Row row = sheet.getRow(idx);
                if (row == null) continue;

                // 거래 일시, 입금액, 내용 파싱
                String payDate = formatter.formatCellValue(row.getCell(columnIndex.get("거래일시")));
                String amountStr = formatter.formatCellValue(row.getCell(columnIndex.get("입금액")));
                String content = formatter.formatCellValue(row.getCell(columnIndex.get("내용")));

                // 비어있거나 무효한 값은 건너뜀
                if (payDate == null || amountStr == null || content == null) continue;
                if (payDate.isBlank() || amountStr.isBlank() || content.isBlank()) continue;
                if (amountStr.equals("0")) continue;

                // 문자열 데이터를 실제 타입으로 변환
                LocalDateTime date = LocalDateTime.parse(payDate, fmt); // 거래일시
                Integer amount = Integer.parseInt(amountStr);            // 입금액
                String paymentFull = content.trim();                     // 입금 내용 (예: "t12김승환")

                // ✅ [1차 시도] payment_name 값이 정확히 일치하는 참여자 목록 조회
                List<EventParticipation> matches = eventParticipationRepository.findAllByPaymentName(paymentFull);

                // 🎯 단 1명과 매칭된 경우 → 곧바로 입금 처리
                if (matches.size() == 1) {
                    EventParticipation ep = matches.get(0);

                    // 이미 납부된 상태가 아니라면 처리 수행
                    if (!Boolean.TRUE.equals(ep.getPaymentStatus())) {
                        ep.setPaidAt(date);         // 납부 일시 등록
                        ep.setPaymentStatus(true);  // 납부 상태 true로 설정
                        eventParticipationRepository.save(ep);

                        // 동일한 시각의 거래가 이미 존재하지 않을 때만 새로운 기록 추가
                        if (!transactionRecordRepository.existsByTxDate(date)) {
                            TransactionRecord record = TransactionRecord.builder()
                                    .amount(amount)
                                    .txDate(date)
                                    .member(ep.getMember())
                                    .expense(false)
                                    .build();
                            transactionRecordRepository.save(record);
                        }

                        // 완료된 사용자 정보 기록 (예: "t12김승환")
                        String eventTitle = ep.getPost().getTitle();
                        String name = ep.getMember().getName();
                        completeUser.add(eventTitle + name);
                    }
                    continue; // 다음 엑셀 행으로
                }

                // 🎯 같은 payment_name으로 여러 명이 매칭된 경우 → 사용자 선택 유도 정보 구성
                if (matches.size() > 1) {
                    for (EventParticipation ep : matches) {
                        Member member = ep.getMember();
                        String key = ep.getPaymentName() + "(" + member.getStudentId() + ")";

                        List<EventDto> events = List.of(
                                EventDto.builder()
                                        .eventName(ep.getPost().getTitle())
                                        .eventId(String.valueOf(ep.getPost().getPostId()))
                                        .build()
                        );

                        // 중복된 사용자 키에 이벤트 누적 추가
                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder()
                                        .events(new ArrayList<>(events))
                                        .studentId(member.getStudentId())
                                        .build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                    continue; // 다음 엑셀 행으로
                }

                // ✅ [2차 fallback] 이름만 추출해서 member 테이블에서 이름 기반 검색
                String nameOnly = content.replaceAll("[^가-힣]", "").trim(); // 예: "김승환"
                if (nameOnly.isBlank()) continue;

                List<Member> foundUsers = userRepository.findByName(nameOnly);

                // ❌ 이름 검색조차 실패 → 실패 목록에 기록
                if (foundUsers.isEmpty()) {
                    userFoundFail.add(nameOnly);
                }
                // ⚠️ 동명이인 존재 → 선택 유도 정보 구성
                else if (foundUsers.size() > 1) {
                    for (Member member : foundUsers) {
                        List<EventParticipation> eventsList = eventParticipationRepository.findByMemberMemberId(member.getMemberId());

                        List<EventDto> events = eventsList.stream().map(ep ->
                                EventDto.builder()
                                        .eventName(ep.getPost().getTitle())
                                        .eventId(String.valueOf(ep.getPost().getPostId()))
                                        .build()
                        ).toList();

                        String key = member.getName() + "(" + member.getStudentId() + ")";
                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder()
                                        .events(new ArrayList<>(events))
                                        .studentId(member.getStudentId())
                                        .build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                }
                // ✅ 이름이 유일하게 일치 → 참여한 이벤트 목록만 반환 (선택 필요)
                else {
                    Member member = foundUsers.get(0);
                    List<EventParticipation> eventsList = eventParticipationRepository.findByMemberMemberId(member.getMemberId());

                    List<EventDto> events = eventsList.stream().map(ep ->
                            EventDto.builder()
                                    .eventName(ep.getPost().getTitle())
                                    .eventId(String.valueOf(ep.getPost().getPostId()))
                                    .build()
                    ).toList();

                    if (!events.isEmpty()) {
                        String key = member.getName();
                        selectedUser.merge(key,
                                ErrorResponseDto.SelectedUserInfo.builder()
                                        .events(new ArrayList<>(events))
                                        .studentId(member.getStudentId())
                                        .build(),
                                (existing, incoming) -> {
                                    existing.getEvents().addAll(incoming.getEvents());
                                    return existing;
                                });
                    }
                }
            }

            // ✅ 최종 결과 DTO 생성 및 반환
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
            // 엑셀 파싱 또는 트랜잭션 저장 중 예외 발생 → IOException으로 감싸서 전달
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
