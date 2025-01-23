package com.dasolsystem.core.post;

import com.dasolsystem.core.post.Dto.RequestRegisterPostDto;
import com.dasolsystem.core.post.Dto.ResponseSavedIdDto;
import com.dasolsystem.core.post.repository.PostRepository;
import com.dasolsystem.core.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;


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
        System.out.println(responseSavedIdDto);
    }
}