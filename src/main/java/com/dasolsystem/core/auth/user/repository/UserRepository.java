package com.dasolsystem.core.auth.user.repository;

import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.enums.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByStudentId(String studentId);

    List<Users> findByName(String name);

    Users findByEmailID(String emailID);

    @Modifying
    @Query("UPDATE Users u SET u.role = :role WHERE u.studentId = :studentId")
    int updateUserRole(@Param("studentId") String studentId, @Param("role") Role role);

//    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.deposits WHERE u.studentId = :studentId AND u.name = :name")
    Optional<Users> findByStudentIdAndName(@Param("studentId") String studentId, @Param("name") String name);

    void deleteByStudentId(String studentId);
    boolean existsByEmailID(String studentId);
    boolean existsBystudentId(String studentId);
    List<Users> findAllByStudentIdIn(List<String> studentIds);
}
