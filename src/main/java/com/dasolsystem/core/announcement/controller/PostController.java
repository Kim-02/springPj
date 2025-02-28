package com.dasolsystem.core.announcement.controller;


import com.dasolsystem.core.announcement.dto.PostRequestDto;
import com.dasolsystem.core.announcement.dto.PostResponseDto;
import com.dasolsystem.core.announcement.service.PostService;
import com.dasolsystem.core.entity.Post;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.dasolsystem.core.jwt.filter.JwtRequestFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announce")
@Slf4j
public class PostController {
    private final PostService postService;
    private final JwtBuilder jwtBuilder;

    
    

    @PostMapping("/create")
    public ResponseEntity<ResponseJson<Object>> create(@RequestBody PostRequestDto requestDto,
                                                       HttpServletRequest servletRequest) {

        try{
            String token = servletRequest.getHeader("Authorization");
            token = token.substring(BEARER_PREFIX.length());
            String username = jwtBuilder.getAccessTokenPayload(token).get("User-Name").toString();
            requestDto.setUsername(username);
            PostResponseDto responseDto = postService.addPost(requestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("Success")
                            .result(responseDto.getResponse())
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    ResponseJson.builder()
                            .status(500)
                            .message("Error")
                            .result(e.getMessage())
                            .build()
            );
        }


    }

    @GetMapping("/postlist")
    public ResponseEntity<ResponseJson<Object>> getPost(){
        try{
            List<Post> allPosts = postService.getAllPosts();
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("Success")
                            .result(allPosts)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    ResponseJson.builder()
                            .status(500)
                            .message("Error")
                            .result(e.getMessage())
                            .build()
            );
        }

    }

    @GetMapping("/postid")
    public ResponseEntity<ResponseJson<Object>> getPostId(@RequestParam("id") Long id){

        Optional<Post> post = postService.getPostById(id);
        return post.map(foundPost ->
                ResponseEntity.ok(
                        ResponseJson.builder()
                                .status(200)
                                .message("success")
                                .result(foundPost)
                                .build()
                )
        ).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                ResponseJson.builder()
                                        .status(404)
                                        .message("Post not found")
                                        .build()
                        )
        );
    }
    @DeleteMapping("/deleteid")
    public ResponseEntity<ResponseJson<Object>> delete(@RequestParam("id") Long id){
        try{
            postService.deletePostById(id);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("Success")
                            .result("Delete "+ id)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    ResponseJson.builder()
                            .status(500)
                            .message("Error")
                            .result(e.getMessage())
                            .build()
            );
        }

    }
}
