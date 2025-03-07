package com.dasolsystem.core.auth.user.repository;

import com.dasolsystem.core.entity.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByStudentId(String studentId);

    List<Users> findByName(String name);

//    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.deposits WHERE u.studentId = :studentId AND u.name = :name")
    Optional<Users> findByStudentIdAndName(@Param("studentId") String studentId, @Param("name") String name);

    void deleteByStudentId(String studentId);
}
