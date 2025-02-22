package com.dasolsystem.core.announcement.repository;

import com.dasolsystem.core.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
