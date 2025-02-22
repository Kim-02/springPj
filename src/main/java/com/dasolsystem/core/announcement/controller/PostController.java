package com.dasolsystem.core.announcement.controller;


import com.dasolsystem.core.announcement.dto.PostRequestDto;
import com.dasolsystem.core.announcement.dto.PostResponseDto;
import com.dasolsystem.core.announcement.service.PostService;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announce")
public class PostController {
    private final PostService postService;
    private final JwtBuilder jwtBuilder;

    
    
    
    //TODO 테스트 코드 작성
    @PostMapping("/create")
    public ResponseEntity<ResponseJson<Object>> create(@RequestBody PostRequestDto requestDto,
                                                       HttpServletRequest servletRequest) {

        try{
            String token = servletRequest.getHeader("Authorization");
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

}
