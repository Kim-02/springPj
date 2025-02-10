package com.dasolsystem.tests.like.service;


import com.dasolsystem.core.entity.UserLike;
import com.dasolsystem.core.redis.reopsitory.RedisLockRepository;
import com.dasolsystem.tests.like.dto.countingDto;
import com.dasolsystem.tests.like.reopsitory.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    private final RedisLockRepository redisLockRepository;

    public void makeLikes() throws InterruptedException {
        for(int i=0;i<100;i+=1){
            while(!redisLockRepository.lock(1L)){
                Thread.sleep(100);
            }
            try{
                UserLike old = likeRepository.findByAuthor("test");
                countingDto count = countingDto.builder()
                        .count(old.getLiked()+1)
                        .build();
                UserLike res = UserLike.builder()
                        .author(old.getAuthor())
                        .id(old.getId())
                        .liked(count.getCount())
                        .build();
                likeRepository.save(res);
            } finally {
                redisLockRepository.unlock(1L);
            }

        }
    }

    public String getCurrnetLikes(){
        return "Current Likes : "+likeRepository.findByAuthor("test").getLiked();
    }
}
