package com.dasolsystem.core.department.dto;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentTreeLeaf {
    private Long memberId;
    private String name;
    private String departmentName;
    private String roleName;
    private Integer roleCode;
}
