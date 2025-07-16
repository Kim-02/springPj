package com.dasolsystem.core.post.eventpost.service;

import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;

public interface EventPostService {
    Long createEventPost(EventPostRequestDto dto);
    Long deleteEventPost(Long postId,String studentId);
    Long updateEventPost(EventPostRequestDto dto,Long postId,String studentId);
    EventPostResponseDto getEventPost(Long postId);
    String participateEventPost(Long postId, String studentId);
}
