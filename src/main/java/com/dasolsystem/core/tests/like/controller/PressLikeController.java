package com.dasolsystem.core.tests.like.controller;

import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.tests.like.service.LikeService;
import com.dasolsystem.core.tests.like.service.RedissonLockStockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PressLikeController {
    private final LikeService likeService;
    private final RedissonLockStockFacade lockStockFacade;
    @GetMapping("/likes")
    public ResponseEntity<ResponseJson<Object>> Likes(){
        String likes = likeService.getCurrnetLikes();
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("OK")
                        .result(likes)
                        .build()
        );
    }
    @GetMapping("/press")
    public void press() throws InterruptedException {
//        likeService.makeLikes();
        lockStockFacade.makeLike();
    }

}
