package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleCode, Long> {
    RoleCode findById(long id);

    Optional<RoleCode> findByCode(String roleCode);
}
