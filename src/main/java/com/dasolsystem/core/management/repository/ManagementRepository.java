package com.dasolsystem.core.management.repository;


import com.dasolsystem.core.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementRepository extends JpaRepository<Course,Long> {

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT c from Course c")
    List<Course> findAllWithUsers();
}
