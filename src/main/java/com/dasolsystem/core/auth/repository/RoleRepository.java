package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findById(long id);

    Optional<Role> findByCode(String roleCode);
}
