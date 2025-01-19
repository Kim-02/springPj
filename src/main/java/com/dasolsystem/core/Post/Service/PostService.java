package com.dasolsystem.core.Post.Service;

import com.dasolsystem.core.Entity.Post;
import com.dasolsystem.core.Post.Dto.RequestRegistrPostDto;
import com.dasolsystem.core.Post.Dto.ResponsePostDto;
import com.dasolsystem.core.Post.Dto.ResponseSavedIdDto;
import com.dasolsystem.core.Post.Repository.PostRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Description("작성하는 메서드")
    public ResponseSavedIdDto write(RequestRegistrPostDto requestDto){ //컨트롤러 단에서 넘겨줄 데이터를 받는 write
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .createdDate(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post); //기본적으로 제공하는 CRUD 메서드 (JPA)

        return ResponseSavedIdDto.builder()
                .savedId(savedPost.getPostId())
                .build();
    }

    @Description("postId를 통해 작성한 글을 불러오는 메서드")
    public ResponsePostDto get(Long postId){
        Post post = postRepository.findById(postId) //Repository에서 기본적으로 제공하는 메서드형 함수(findByid)
                .orElseThrow(() -> new RuntimeException("없는 게시글입니다."));
        return ResponsePostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content((post.getContent()))
                .createdDate(post.getCreatedDate())
                .build();
    }
}
