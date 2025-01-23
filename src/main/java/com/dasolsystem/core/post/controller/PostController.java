package com.dasolsystem.core.post.controller;

import com.dasolsystem.core.post.Dto.*;
import com.dasolsystem.core.post.service.PostService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @GetMapping("/test/testpost")
    public ResponseEntity<ResponseJson<Object>> testPost(){

        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result("testPost")
                        .build()
        );
    }
    @PostMapping
    public ResponseEntity<ResponseJson<Object>> registerPost(@RequestBody RequestRegisterPostDto requsetDto){
        ResponseSavedIdDto responseSavedIdDto = postService.write(requsetDto);



        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(responseSavedIdDto)
                .build()
        );
    }

    @Description("postid를 통해 게시물을 받아오는 API")
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseJson<Object>> getPost(@PathVariable Long postId){
        ResponsePostDto responsePostDto = postService.get(postId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(responsePostDto)
                        .build()
        );
    }
    @PutMapping("/{postId}")
    public ResponseEntity<ResponseJson<Object>> updatePost(@PathVariable Long postId, @RequestBody RequestUpdatePostDto requestUpdatePostDto){
        postService.edit(postId, requestUpdatePostDto);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result("OK")
                        .build()
        );
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseJson<Object>> deletePost(@PathVariable Long postId){
        postService.delete(postId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(postId+" 삭제 완료")
                        .build()
        );
    }
    @GetMapping
    public ResponseEntity<ResponseJson<Object>> getPostList(RequestListDto requestListDto){
        ResponsePostListDto postList = postService.getList(requestListDto);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(postList)
                        .build()
        );
    }
}
