package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface authRepository extends JpaRepository<Users,Long> {
    Users findByEmailID(String id);
    boolean existsByEmailID(String id);
}
