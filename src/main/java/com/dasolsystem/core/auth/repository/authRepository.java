package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface authRepository extends JpaRepository<Member,Long> {

}
