package com.dasolsystem.tests;

import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import com.dasolsystem.core.jwt.repository.RedisJwtRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class RedisTestController {

    @Autowired
    private RedisJwtRepository redisJwtRepository;

    @PostMapping("/redis")
    public String reids(@RequestBody TokenIdAccesserDto tokenIdAccesserDto){
        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Long.parseLong(tokenIdAccesserDto.getTokenId()));
        if(optionalRedisJwtId.isPresent()){
            RedisJwtId redisid = optionalRedisJwtId.get();
            return redisid.getJwtToken();
        }
        return null;
    }

}
