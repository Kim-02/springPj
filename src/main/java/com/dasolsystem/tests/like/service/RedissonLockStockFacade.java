package com.dasolsystem.tests.like.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockStockFacade {
    private RedissonClient redisson;
    private LikeService likeService;

    @Autowired
    public RedissonLockStockFacade(RedissonClient redisson, LikeService likeService) {
        this.redisson = redisson;
        this.likeService = likeService;
    }
    public void makeLike(){
        RLock lock = redisson.getLock("like");
        try{
            boolean available = lock.tryLock(20,1, TimeUnit.SECONDS);

            if(!available){
                log.error("✔ lock fail");
                return;
            }
            likeService.makeLikesRedisson();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }
}
