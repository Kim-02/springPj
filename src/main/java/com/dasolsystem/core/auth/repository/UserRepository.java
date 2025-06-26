package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStudentId(String studentId);

    List<Member> findByName(String name);

    Member findByEmailID(String emailID);
    @Modifying
    @Query("UPDATE Member u SET u.role = :role WHERE u.studentId = :studentId")
    int updateUserRole(@Param("studentId") String studentId, @Param("role") Role role);

//    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.deposits WHERE u.studentId = :studentId AND u.name = :name")
    Optional<Member> findByStudentIdAndName(@Param("studentId") String studentId, @Param("name") String name);

    void deleteByStudentId(String studentId);
    boolean existsByEmailID(String studentId);
    boolean existsBystudentId(String studentId);
    List<Member> findAllByStudentIdIn(List<String> studentIds);
}
