package com.dasolsystem.core.post.repository;

import com.dasolsystem.core.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

}
