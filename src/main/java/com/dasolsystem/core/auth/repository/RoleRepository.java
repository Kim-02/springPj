package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findById(long id);
}
