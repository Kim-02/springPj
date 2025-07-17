package com.dasolsystem.core.department.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentTreeNode {
    private List<DepartmentTreeNode> nodes;
    private List<DepartmentTreeLeaf> children;
    private DepartmentTreeLeaf leaf;

    public void setNode(DepartmentTreeNode node) {
        this.nodes.add(node);
    }
    public void setChildren(DepartmentTreeLeaf children) {
        this.children.add(children);
    }

}
