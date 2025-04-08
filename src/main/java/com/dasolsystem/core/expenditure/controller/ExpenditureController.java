package com.dasolsystem.core.expenditure.controller;


import com.dasolsystem.core.entity.Expenditure;
import com.dasolsystem.core.expenditure.dto.ExpenditureRequestDto;
import com.dasolsystem.core.expenditure.dto.ExpenditureResponseDto;
import com.dasolsystem.core.expenditure.service.ExpenditureService;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expend")
@RequiredArgsConstructor
public class ExpenditureController {
    private final ExpenditureService expenditureService;

    //출금 내역 업데이트
    @PostMapping("/update")
    public ResponseEntity<ResponseJson<Object>> update(@ModelAttribute ExpenditureRequestDto expenditureRequestDto) {
        try{
            List<ExpenditureResponseDto> responseDto = expenditureService.saveExpendituresFromExcel(expenditureRequestDto.getMultipartFile());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(responseDto)
                            .build()
            );
        }catch (Exception e){
            return  ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(501)
                            .message("error")
                            .result(e.getMessage())
                            .build()
            );
        }



    }
}
