package com.dasolsystem.core.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentOrgTreeDto {
    private Long id;
    private Long parentId;
    private String name;
    private String departmentName;
    private Integer roleCode;
    private Integer depth;
    @Builder.Default
    List<DepartmentOrgTreeDto> children = new ArrayList<>();

}
