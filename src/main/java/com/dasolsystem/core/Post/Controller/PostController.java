package com.dasolsystem.core.Post.Controller;

import com.dasolsystem.core.Entity.Post;
import com.dasolsystem.core.Post.Dto.*;
import com.dasolsystem.core.Post.Service.PostService;
import jakarta.websocket.OnClose;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseJson<Object>> registerPost(@RequestBody RequestRegistrPostDto requsetDto){
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
    public ResponseEntity<ResponseJson<Object>> getPostList(@ModelAttribute RequestListDto requestListDto){
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
