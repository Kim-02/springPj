package com.dasolsystem.core.post.inquirypost.controller;

import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import com.dasolsystem.core.post.inquirypost.dto.InquiryPostRequestDto;
import com.dasolsystem.core.post.inquirypost.dto.InquiryPostResponseDto;
import com.dasolsystem.core.post.inquirypost.service.InquiryPostService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiry_post")
@RequiredArgsConstructor
public class InquiryPostController {
    private final InquiryPostService inquiryPostService;
    private final SecurityGuardian securityGuardian;

    @PostMapping("/create")
    public ResponseEntity<ResponseJson<?>> createPost(@RequestBody InquiryPostRequestDto inquiryPostRequestDto, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            inquiryPostRequestDto.setStudentId(loginClaim.getSubject());
            Long postId = inquiryPostService.createInquiryPost(inquiryPostRequestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success post id: "+ postId)
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
            Long deleted_id = inquiryPostService.deleteInquiryPost(post_id,loginClaim.getSubject());
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
    public ResponseEntity<ResponseJson<?>> updatePost(@RequestBody InquiryPostRequestDto inquiryPostRequestDto, @PathVariable Long postId, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            Long updated_id = inquiryPostService.updateInquiryPost(inquiryPostRequestDto,postId,loginClaim.getSubject());
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("updated"+updated_id)
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
    @GetMapping("/posts/get/{postId}")
    public ResponseEntity<ResponseJson<?>> getPost(@PathVariable Long postId) {
        try{
            InquiryPostResponseDto responseDto = inquiryPostService.getInquiryPost(postId);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success")
                            .result(responseDto)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ResponseJson.builder().status(500).message(e.getMessage()).build()
            );
        }

    }
}
