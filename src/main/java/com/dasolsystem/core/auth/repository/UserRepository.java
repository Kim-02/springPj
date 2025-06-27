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
    boolean existsBystudentId(String studentId);
}
