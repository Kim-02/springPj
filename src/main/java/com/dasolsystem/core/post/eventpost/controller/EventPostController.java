package com.dasolsystem.core.post.eventpost.controller;

import com.dasolsystem.core.entity.EventPost;
import com.dasolsystem.core.entity.Post;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import com.dasolsystem.core.post.eventpost.service.EventPostService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event_post")
@RequiredArgsConstructor
public class EventPostController {
    private final EventPostService eventPostService;
    private final SecurityGuardian securityGuardian;

    @PostMapping("/create")
    public ResponseEntity<ResponseJson<?>> createPost(@RequestBody EventPostRequestDto eventPostRequestDto, HttpServletRequest request) {
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            eventPostRequestDto.setStudentId(loginClaim.getSubject());
            Long postId = eventPostService.createEventPost(eventPostRequestDto);
            return ResponseEntity.ok(
                    ResponseJson.builder()
                            .status(200)
                            .message("success post memberId: "+ postId)
                    .build()
            );

    }
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseJson<?>> deletePost(@RequestParam Long post_id, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            Long deleted_id = eventPostService.deleteEventPost(post_id,loginClaim.getSubject());
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
    public ResponseEntity<ResponseJson<?>> updatePost(@RequestBody EventPostRequestDto eventPostRequestDto,@PathVariable Long postId, HttpServletRequest request) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        if(loginClaim == null) throw new IllegalStateException("Login claim is null");
        Long updated_id = eventPostService.updateEventPost(eventPostRequestDto,postId,loginClaim.getSubject());
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("updated"+updated_id)
                        .build()
        );
    }
    @GetMapping("/posts/get/{postId}")
    public ResponseEntity<ResponseJson<?>> getPost(@PathVariable Long postId) {
        try{
            EventPostResponseDto responseDto = eventPostService.getEventPost(postId);
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
    @PostMapping("/posts/{postId}/participate")
    public ResponseEntity<ResponseJson<?>> participatePost(@PathVariable Long postId, HttpServletRequest request) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        String participant = eventPostService.participateEventPost(postId,loginClaim.getSubject());
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success participaint "+participant)
                .build()
        );
    }
}
