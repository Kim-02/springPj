package com.dasolsystem.core.auth.user.repository;

import com.dasolsystem.core.entity.PaidMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaidMemberRepository extends JpaRepository<PaidMember, Long> {
}
