package com.dasolsystem.core.Post.Service;

import com.dasolsystem.core.Entity.Post;
import com.dasolsystem.core.Post.Dto.*;
import com.dasolsystem.core.Post.Repository.PostRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Description("작성하는 메서드")
    public ResponseSavedIdDto write(RequestRegisterPostDto requestDto){ //컨트롤러 단에서 넘겨줄 데이터를 받는 write
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

    @Description("게시글 수정하는 메서드")
    @Transactional
    public void edit(Long postId, RequestUpdatePostDto requestDto){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다. "));
        post.update(requestDto);
    }

    @Description("게시글 삭제하는 메서드")
    public void delete(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 게시글입니다"));
        postRepository.delete(post); //기본적으로 제공해주는 CRUD

    }

    public ResponsePostListDto getList(RequestListDto requestListDto){
        PageRequest pageRequest = PageRequest.of(requestListDto.getPage(), requestListDto.getPageSize());
        Page<Post> posts = postRepository.findAll(pageRequest);
        ResponsePostListDto responseDto = ResponsePostListDto.builder()
                .totalCount((int) posts.getTotalElements())
                .page(posts.getNumber())
                .pageSize(posts.getSize())
                .postList(posts.stream().map(post -> ResponsePostDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .createdDate(post.getCreatedDate())
                        .build()).collect(Collectors.toList()))
                .build();
        return responseDto;
    }
}
