package com.dasolsystem.core.jparepository;

import com.dasolsystem.core.entity.SignUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface authRepository extends JpaRepository<SignUp,Long> {
    SignUp findByEmailID(String id);
    boolean existsByEmailID(String id);
}
