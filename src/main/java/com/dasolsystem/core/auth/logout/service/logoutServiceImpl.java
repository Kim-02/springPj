package com.dasolsystem.core.auth.logout.service;


import com.dasolsystem.core.entity.RedisJwtId;
import com.dasolsystem.core.jwt.dto.TokenIdAccesserDto;
import com.dasolsystem.core.jwt.repository.RedisJwtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class logoutServiceImpl implements logoutService {
    private RedisJwtRepository redisJwtRepository;

//    public String exicuteLogout(Long Id){
//        Optional<RedisJwtId> optionalRedisJwtId = redisJwtRepository.findById(Id);
//        if(optionalRedisJwtId.isPresent()){
//            RedisJwtId redisJwtId = optionalRedisJwtId.get();
//            TokenIdAccesserDto tokenIdAccesserDto = TokenIdAccesserDto.builder()
//                    .tokenId(Id)
//                    .
//        }
//
//    }
}
