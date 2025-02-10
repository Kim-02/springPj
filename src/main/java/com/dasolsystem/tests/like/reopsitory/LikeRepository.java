package com.dasolsystem.tests.like.reopsitory;

import com.dasolsystem.core.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<UserLike,Long> {
    UserLike findByAuthor(String author);
}
