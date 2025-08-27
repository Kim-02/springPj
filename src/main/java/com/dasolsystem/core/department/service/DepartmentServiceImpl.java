package com.dasolsystem.core.department.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.department.dto.DepartmentOrgTreeDto;
import com.dasolsystem.core.department.repository.DepartmentRepository;
import com.dasolsystem.core.enums.ApiState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    private static final Comparator<DepartmentOrgTreeDto> ORG_COMPARATOR =
            Comparator.comparing(DepartmentOrgTreeDto::getRoleCode, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(DepartmentOrgTreeDto::getDepartmentName, Comparator.nullsLast(String::compareTo))
                    .thenComparing(DepartmentOrgTreeDto::getName, Comparator.nullsLast(String::compareTo));

    @Transactional(readOnly = true)
    public DepartmentOrgTreeDto getDepartmentTree(){
        List<MemberHierarchyView> rows = departmentRepository.fetchHierarchy();
        if(rows.isEmpty()){
            throw new DBFaillException(ApiState.ERROR_500,"DB 정보를 찾지 못했습니다");
        }
        Map<Long,DepartmentOrgTreeDto> map = new LinkedHashMap<>();
        DepartmentOrgTreeDto root = null;
        for(MemberHierarchyView row : rows){
            DepartmentOrgTreeDto node = map.computeIfAbsent(row.getMemberId(),id -> toNode(row));

            node.setParentId(row.getParentId());
            node.setDepth(row.getDepth());
            node.setName(row.getName());
            node.setRoleCode(row.getRoleCode());
            node.setDepartmentName(row.getDepartmentName());

            Long parentId = row.getParentId();
            if(parentId == null){
                root = node;
                continue;
            }
            DepartmentOrgTreeDto parent = map.get(parentId);
            if(parent == null){
                parent = DepartmentOrgTreeDto.builder()
                        .parentId(parentId)
                        .children(new ArrayList<>())
                        .build();
                map.put(parentId,parent);
            }
            parent.getChildren().add(node);
        }
        sortRecursively(root);
        return root;
    }

    private DepartmentOrgTreeDto toNode(MemberHierarchyView row){
        return DepartmentOrgTreeDto.builder()
                .id(row.getMemberId())
                .parentId(row.getParentId())
                .name(row.getName())
                .roleCode(row.getRoleCode())
                .departmentName(row.getDepartmentName())
                .children(new ArrayList<>())
                .depth(row.getDepth())
                .build();
    }

    private void sortRecursively(DepartmentOrgTreeDto n){
        if(n == null || n.getChildren() == null) return;
        n.getChildren().sort(ORG_COMPARATOR);
        for(DepartmentOrgTreeDto child : n.getChildren()){sortRecursively(child);}
    }
}
