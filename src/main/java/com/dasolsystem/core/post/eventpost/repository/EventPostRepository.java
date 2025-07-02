package com.dasolsystem.core.post.eventpost.repository;

import com.dasolsystem.core.entity.EventPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPostRepository extends JpaRepository<EventPost, Long> {

}
