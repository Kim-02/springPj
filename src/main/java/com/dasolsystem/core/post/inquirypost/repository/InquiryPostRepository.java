package com.dasolsystem.core.post.inquirypost.repository;

import com.dasolsystem.core.entity.InquiryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface InquiryPostRepository extends JpaRepository<InquiryPost, Long> {
}
