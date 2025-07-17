package com.dasolsystem.core.user.repository;

import com.dasolsystem.core.entity.PermissionChange;
import com.dasolsystem.core.entity.PermissionChangeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionChangeRepository extends JpaRepository<PermissionChange, PermissionChangeId> {
}
