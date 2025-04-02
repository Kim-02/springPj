package com.dasolsystem.core.approval.controller;

import com.dasolsystem.core.entity.Approval;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {

    @PostMapping("/post")
    public ResponseEntity<ResponseJson<Object>> post(@RequestBody Approval approval) {}
}
