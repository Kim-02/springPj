package com.dasolsystem.core.Post.Controller;

import com.dasolsystem.core.Entity.Post;
import com.dasolsystem.core.Post.Dto.RequestRegistrPostDto;
import com.dasolsystem.core.Post.Dto.ResponsePostDto;
import com.dasolsystem.core.Post.Dto.ResponseSavedIdDto;
import com.dasolsystem.core.Post.Service.PostService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResponseSavedIdDto> registerPost(@RequestBody RequestRegistrPostDto requsetDto){
        ResponseSavedIdDto responseSavedIdDto = postService.write(requsetDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{savedId}")
                .buildAndExpand(responseSavedIdDto.getSavedId())
                .toUri();

        return ResponseEntity.created(location).body(responseSavedIdDto);
    }

//    @Description("postid를 통해 게시물을 받아오는 API")
//    @GetMapping("/{postId}")
//    public ResponseEntity<ResponsePostDto> getPost(@PathVariable Long postId){
//        ResponsePostDto responsePostDto = postService.get(postId);
//    }
}
