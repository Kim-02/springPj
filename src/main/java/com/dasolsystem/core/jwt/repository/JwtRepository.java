package com.dasolsystem.core.jwt.repository;

import com.dasolsystem.core.entity.SignUpJwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<SignUpJwt, String> {
    SignUpJwt findByusername(String username);
}
