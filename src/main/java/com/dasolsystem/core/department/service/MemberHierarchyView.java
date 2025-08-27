package com.dasolsystem.core.department.service;

public interface MemberHierarchyView {
    Long getMemberId();
    Long getParentId();
    String getName();
    String getDepartmentName();
    Integer getRoleCode();
    Integer getDepth();
}
