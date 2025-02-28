package com.dasolsystem.core.announcement.service;

import com.dasolsystem.core.announcement.dto.PostRequestDto;
import com.dasolsystem.core.announcement.dto.PostResponseDto;
import com.dasolsystem.core.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    PostResponseDto addPost(PostRequestDto postRequestDto);
    List<Post> getAllPosts();
    Optional<Post> getPostById(Long id);
    void deletePostById(Long id);
}
