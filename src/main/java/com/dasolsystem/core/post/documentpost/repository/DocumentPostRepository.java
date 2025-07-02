package com.dasolsystem.core.post.documentpost.repository;

import com.dasolsystem.core.entity.DocumentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentPostRepository extends JpaRepository<DocumentPost, Long> {
}
