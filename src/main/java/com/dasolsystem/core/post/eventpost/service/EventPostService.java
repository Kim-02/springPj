package com.dasolsystem.core.post.eventpost.service;

import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;

public interface EventPostService {
    Long createEventPost(EventPostRequestDto dto);
}
