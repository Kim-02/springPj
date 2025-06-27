package com.dasolsystem.core.auth.repository;

import com.dasolsystem.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
    @Query("select u from Member u join fetch u.role where u.studentId = :studentId")
    Optional<Member> findByStudentIdWithRole(String studentId);
    //TODO 같은 트랜잭션 안에서 불러오지 않았기 때문에 오류가 inconecction 오류가 발생하였었음. 해결 방법은 join으로 불러옴으로
    //로딩 지연을 해결하였음
    boolean existsBystudentId(String studentId);

    Optional<Member> findByStudentId(String studentId);
}
