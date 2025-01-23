package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.SignUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface authRepository extends JpaRepository<SignUp,Long> {
    SignUp findByEmailID(String id);
}
