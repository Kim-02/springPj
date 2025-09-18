package com.dasolsystem.core.user.repository;

import com.dasolsystem.core.entity.PaidUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaidUserRepository extends JpaRepository<PaidUsers,Long> {
    Optional<PaidUsers> findByStudentId(String cellValue);
}
