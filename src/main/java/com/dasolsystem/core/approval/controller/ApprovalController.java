//package com.dasolsystem.core.approval.controller;
//
//import com.dasolsystem.config.excption.FileException;
//import com.dasolsystem.core.approval.dto.ApprovalPostDto;
//import com.dasolsystem.core.approval.dto.ApprovalSummaryDto;
//import com.dasolsystem.core.approval.service.ApprovalService;
//import com.dasolsystem.core.entity.Approval;
//import com.dasolsystem.core.handler.ResponseJson;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/approval")
//public class ApprovalController {
//    private final ApprovalService approvalService;
//
//    @PostMapping("/post")
//    public ResponseEntity<ResponseJson<Object>> post(@ModelAttribute ApprovalPostDto approvalPostDto) {
//        try{
//            Long returnId = approvalService.saveApprovePost(approvalPostDto);
//            return ResponseEntity.ok(
//                    ResponseJson.builder()
//                            .status(200)
//                            .message("success")
//                            .result(returnId)
//                            .build()
//            );
//        } catch (FileException | IOException e) {
//            return ResponseEntity.ok(
//                    ResponseJson.builder()
//                            .status(801)
//                            .message("file upload failed")
//                            .result("upload failed")
//                            .build()
//            );
//        }
//
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<ResponseJson<Object>> getApprovalLists() {
//        try{
//            List<ApprovalSummaryDto> summaries = approvalService.getApprovalSummaries();
//            return ResponseEntity.ok(
//                    ResponseJson.builder()
//                            .status(200)
//                            .message("success")
//                            .result(summaries)
//                            .build()
//            );
//        }catch (Exception e){
//            return ResponseEntity.ok(
//                    ResponseJson.builder()
//                            .status(501)
//                            .message("error")
//                            .result(e.getMessage())
//                            .build()
//            );
//        }
//
//    }
//
////    @PostMapping("/getpersonal")
////    public ResponseEntity<ResponseJson<Object>> getPersonalApprovalPage() {
////        try{
////
////        }
////    }
//}
