package com.dasolsystem.core.post;

import com.dasolsystem.core.auth.repository.authRepository;
import com.dasolsystem.core.auth.signup.Dto.RequestSignupPostDto;
import com.dasolsystem.core.auth.signup.service.signupService;
import com.dasolsystem.core.post.Dto.RequestRegisterPostDto;
import com.dasolsystem.core.post.Dto.ResponseSavedIdDto;
import com.dasolsystem.core.post.repository.PostRepository;
import com.dasolsystem.core.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private authRepository authRepository;

    @Autowired
    private signupService signupService;
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

    @Test
    @DisplayName("회원 추가")
    void addSignUpTest() {
        if(authRepository.findByEmailID("1234")==null){
            RequestSignupPostDto requestDto = RequestSignupPostDto.builder()
                    .email("1234")
                    .password("1234")
                    .userName("sw1")
                    .build();

            signupService.signup(requestDto);
        }

        System.out.println(authRepository.findByEmailID("1234"));
    }
}