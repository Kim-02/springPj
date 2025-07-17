package com.dasolsystem.core.department.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.department.dto.DepartmentTreeLeaf;
import com.dasolsystem.core.department.dto.DepartmentTreeNode;
import com.dasolsystem.core.department.repository.DepartmentRepository;
import com.dasolsystem.core.entity.Department;
import com.dasolsystem.core.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DepartmentTreeNode getDepartmentTree() {
        // 1) 모든 회원을 한 번에 조회 (department, role 관계 fetch 조인 추천)
        List<Member> members = userRepository.findAllWithDeptAndRole();
        //    .findAllFetchDepartmentAndRole() 같은 커스텀 쿼리로 N+1 방지

        // 2) DTO 변환 & 역할별 그룹핑
        List<DepartmentTreeLeaf> allLeaves = members.stream()
                .map(m -> DepartmentTreeLeaf.builder()
                        .memberId(m.getMemberId())
                        .name(m.getName())
                        .departmentName(m.getDepartment().getDepartmentRole())
                        .roleName(m.getRole().getName())
                        .roleCode(Integer.valueOf(m.getRole().getCode()))
                        .build())
                .toList();

        Map<Integer, List<DepartmentTreeLeaf>> byRole = allLeaves.stream()
                .collect(Collectors.groupingBy(DepartmentTreeLeaf::getRoleCode));

        List<DepartmentTreeLeaf> level200 = byRole.getOrDefault(200, List.of());
        List<DepartmentTreeLeaf> level201 = byRole.getOrDefault(201, List.of());
        List<DepartmentTreeLeaf> level202 = byRole.getOrDefault(202, List.of());

        // 3) Root 노드 (200 중 첫 번째) 설정, 비어 있으면 빈 노드 반환
        DepartmentTreeNode root = new DepartmentTreeNode(new ArrayList<>(), new ArrayList<>(), null);
        if (!level200.isEmpty()) {
            DepartmentTreeLeaf rootLeaf = level200.get(0);
            root.setLeaf(rootLeaf);
            // 나머지 200 은 children 으로
            level200.stream().skip(1).forEach(root::setChildren);
        }

        // 4) 201 계층: 부서 이름 키로 Node 생성 & 맵에 저장
        Map<String, DepartmentTreeNode> nodes201Map = level201.stream()
                .map(leaf201 -> DepartmentTreeNode.builder()
                        .nodes(new ArrayList<>())
                        .children(new ArrayList<>())
                        .leaf(leaf201)
                        .build())
                .collect(Collectors.toMap(
                        n -> n.getLeaf().getDepartmentName(),
                        Function.identity(),
                        // 같은 부서명이 중복될 땐 충돌 해결(첫 번째만 사용)
                        (existing, ignored) -> existing
                ));

        // root 아래 201 노드들 붙이기
        nodes201Map.values().forEach(root::setNode);

        // 5) 202 계층: departmentName 기준으로 해당 201 Node 의 children 에 추가
        level202.forEach(leaf202 -> {
            DepartmentTreeNode parent201 = nodes201Map.get(leaf202.getDepartmentName());
            if (parent201 != null) {
                parent201.setChildren(leaf202);
            }
        });

        return root;
    }

}
