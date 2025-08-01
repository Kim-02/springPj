package com.dasolsystem.core.department.repository;

import com.dasolsystem.core.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    /**
     * 학과명(departmentRole)을 기준으로 학과 엔티티를 조회하기 위한 메서드 추가
     *
     * JpaRepository에는 기본적으로 findById만 제공되므로,
     * departmentRole이라는 필드명 기준으로 조회하려면
     * 반드시 직접 메서드를 선언해줘야 해서 추가함.
     *
     * 사용 예:
     * departmentRepository.findByDepartmentRole("컴퓨터공학부")
     */
    Optional<Department> findByDepartmentRole(String departmentRole);
}
