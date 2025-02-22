package com.dasolsystem.core.announcement.service;

import com.dasolsystem.core.announcement.dto.PostRequestDto;
import com.dasolsystem.core.announcement.dto.PostResponseDto;

public interface PostService {
    PostResponseDto addPost(PostRequestDto postRequestDto);
}
