package com.dasolsystem.core.department.repository;

import com.dasolsystem.core.department.service.MemberHierarchyView;
import com.dasolsystem.core.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    @Query(value = """
        WITH RECURSIVE
            root_200 AS (
                SELECT m.member_id
                FROM member m
                         JOIN role_code r ON r.role_code = m.role_code
                WHERE r.role_code = 200
                ORDER BY m.member_id
                LIMIT 1
            ),
            all_nodes AS (
                SELECT
                    m.member_id,
                    m.name,
                    d.department_role AS department_name,
                    r.role_code AS role_code,
                    CASE
                        WHEN r.role_code = 200 AND m.member_id = (SELECT member_id FROM root_200) THEN NULL
                        WHEN r.role_code = 200 THEN (SELECT member_id FROM root_200)
                        WHEN r.role_code = 201 THEN (SELECT member_id FROM root_200)
                        WHEN r.role_code = 202 THEN (
                            SELECT m201.member_id
                            FROM member m201
                                     JOIN role_code r201 ON r201.role_code = m201.role_code AND r201.role_code = 201
                                     JOIN department d201 ON d201.department_role = m201.department_role
                            WHERE d201.department_role = d.department_role
                            ORDER BY m201.member_id
                            LIMIT 1
                        )
                        ELSE NULL
                        END AS parent_id
                FROM member m
                         JOIN role_code r   ON r.role_code = m.role_code
                         JOIN department d  ON d.department_role = m.department_role
            ),
            tree AS (
                SELECT
                    n.member_id, n.name, n.department_name, n.role_code,
                    n.parent_id,
                    0 AS depth
                FROM all_nodes n
                WHERE n.parent_id IS NULL
        
                UNION ALL
        
                SELECT
                    c.member_id, c.name, c.department_name, c.role_code,
                    c.parent_id,
                    t.depth + 1 AS depth
                FROM all_nodes c
                         JOIN tree t ON c.parent_id = t.member_id
            )
        SELECT
            member_id, parent_id, name, department_name, role_code, depth
        FROM tree
        where role_code !=100
        order by parent_id;
    """,nativeQuery = true)
    List<MemberHierarchyView> fetchHierarchy();
}
