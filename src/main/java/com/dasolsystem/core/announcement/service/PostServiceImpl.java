package com.dasolsystem.core.announcement.service;


import com.dasolsystem.core.announcement.dto.PostRequestDto;
import com.dasolsystem.core.announcement.dto.PostResponseDto;
import com.dasolsystem.core.announcement.repository.PostRepository;
import com.dasolsystem.core.entity.Post;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;

    @Transactional
    public PostResponseDto addPost(PostRequestDto postRequestDto) {
        Post responsePost = postRepository.save(Post.builder()
                        .title(postRequestDto.getTitle())
                        .username(postRequestDto.getUsername())
                        .content(postRequestDto.getContent())
                        .access_role(postRequestDto.getSelectrole())
                        .createdAt(LocalDate.now())
                        .build());
        return PostResponseDto.builder()
                .response(responsePost.getTitle())
                .build();
    }

    @Transactional
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    @Transactional
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}
