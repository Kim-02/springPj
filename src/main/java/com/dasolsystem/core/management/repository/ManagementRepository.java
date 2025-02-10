package com.dasolsystem.core.management.repository;


import com.dasolsystem.core.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Course,Long> {

}
