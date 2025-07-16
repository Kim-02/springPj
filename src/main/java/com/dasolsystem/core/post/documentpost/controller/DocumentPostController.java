package com.dasolsystem.core.post.documentpost.controller;

import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.post.documentpost.dto.DocumentPostRequestDto;
import com.dasolsystem.core.post.documentpost.dto.DocumentPostResponseDto;
import com.dasolsystem.core.post.documentpost.service.DocumentPostService;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentPostController {
    private final DocumentPostService documentPostService;
    private final SecurityGuardian securityGuardian;

    @PostMapping("/create")
    public ResponseEntity<ResponseJson<?>> createPost(@RequestBody DocumentPostRequestDto documentPostRequestDto, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            documentPostRequestDto.setStudentId(loginClaim.getSubject());
            Long postId = documentPostService.createDocumentPost(documentPostRequestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success post memberId: "+ postId)
                            .build()
            );
        }catch(Exception e){
            return ResponseEntity.status(500).body(
                    ResponseJson.builder()
                            .status(500)
                            .message("failer. Error. "+ e.getMessage())
                            .build()

            );
        }

    }
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseJson<?>> deletePost(@RequestParam Long post_id, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            Long deleted_id = documentPostService.deleteDocumentPost(post_id,loginClaim.getSubject());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("deleted"+deleted_id)
                            .build()
            );
        }catch(Exception e){
            return ResponseEntity.status(500).body(
                    ResponseJson.builder()
                            .status(500)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ResponseJson<?>> updatePost(@RequestBody DocumentPostRequestDto documentPostRequestDto, @PathVariable Long postId, HttpServletRequest request) {
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            Long updated_id = documentPostService.updateDocumentPost(documentPostRequestDto,postId,loginClaim.getSubject());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("updated"+updated_id)
                            .build()
            );
    }
    @GetMapping("/posts/get/{postId}")
    public ResponseEntity<ResponseJson<?>> getPost(@PathVariable Long postId) {
            DocumentPostResponseDto responseDto = documentPostService.getDocumentPost(postId);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(responseDto)
                            .build()
            );
    }

    @GetMapping("/getAllPost")
    public ResponseEntity<ResponseJson<?>> getAllPost() {
        List<DocumentPostResponseDto> responseDtos = documentPostService.getDocumentPosts();
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success loading")
                        .result(
                                responseDtos
                        )
                .build()
        );
    }
}
