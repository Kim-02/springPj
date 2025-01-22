package com.dasolsystem.core.post;

import com.dasolsystem.core.Entity.Post;
import com.dasolsystem.core.Post.Dto.RequestRegisterPostDto;
import com.dasolsystem.core.Post.Dto.ResponseSavedIdDto;
import com.dasolsystem.core.Post.Repository.PostRepository;
import com.dasolsystem.core.Post.Service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 쓰기")
    void postTest() {
        // given
        RequestRegisterPostDto requestDto = RequestRegisterPostDto.builder()
                .title("test title")
                .content("test content")
                .build();

        // when
        ResponseSavedIdDto responseSavedIdDto = postService.write(requestDto);

        // then
        Post post = postRepository.findById(responseSavedIdDto.getSavedId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        assertEquals(responseSavedIdDto.getSavedId(), post.getPostId());
        assertEquals("test title", post.getTitle());
        assertEquals("test content", post.getContent());
    }
}