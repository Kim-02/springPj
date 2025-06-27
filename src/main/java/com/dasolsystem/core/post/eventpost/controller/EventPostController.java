package com.dasolsystem.core.post.eventpost.controller;

import com.dasolsystem.core.entity.EventPost;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.service.EventPostService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event_post")
@RequiredArgsConstructor
public class EventPostController {
    private final EventPostService eventPostService;
    private final SecurityGuardian securityGuardian;

    @PostMapping("/create")
    public ResponseEntity<ResponseJson<?>> createPost(@RequestBody EventPostRequestDto eventPostRequestDto, HttpServletRequest request) {
        try{
            Claims loginClaim = securityGuardian.getServletTokenClaims(request);
            if(loginClaim == null) throw new IllegalStateException("Login claim is null");
            eventPostRequestDto.setStudentId(loginClaim.getSubject());
            Long postId = eventPostService.createEventPost(eventPostRequestDto);
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
}
