package com.dasolsystem.core.management.service;


import com.dasolsystem.core.entity.Course;
import com.dasolsystem.core.entity.Users;
import com.dasolsystem.core.management.repository.ManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagementServiceImpl implements ManagementService {
    private final ManagementRepository managementRepository;


    public void printAllCourseWithUsers(){
        List<Course> courses = managementRepository.findAll();
        for (Course course : courses) {
            log.info("course name: "+course.getName());
            for(Users users : course.getUsers()){
                log.info("-"+ users.getUserName());
            }
        }
    }

}
