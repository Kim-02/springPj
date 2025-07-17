package com.dasolsystem.core.department.controller;

import com.dasolsystem.core.department.dto.DepartmentTreeNode;
import com.dasolsystem.core.department.service.DepartmentService;
import com.dasolsystem.core.handler.ResponseJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/tree")
    public ResponseEntity<ResponseJson<?>> tree() {
        DepartmentTreeNode root = departmentService.getDepartmentTree();
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("department tree")
                        .result(root)
                        .build()
        );
    }
}
