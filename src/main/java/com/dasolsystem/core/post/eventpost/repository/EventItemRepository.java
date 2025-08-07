package com.dasolsystem.core.post.eventpost.repository;

import com.dasolsystem.core.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventItemRepository extends JpaRepository<EventItem, Long> {
}
