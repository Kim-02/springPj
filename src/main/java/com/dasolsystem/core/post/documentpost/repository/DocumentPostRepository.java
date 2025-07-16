package com.dasolsystem.core.post.documentpost.repository;

import com.dasolsystem.core.entity.DocumentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentPostRepository extends JpaRepository<DocumentPost, Long> {
    @Query("""
    select dp
      from DocumentPost dp
      join fetch dp.post p
      join fetch p.member m
  """)
    List<DocumentPost> findAllWithPostAndMember();
}
