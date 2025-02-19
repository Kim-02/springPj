package com.dasolsystem.core.tests.account.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.dasolsystem.config.excption.JsonFailException;
import com.dasolsystem.core.document.AccountES;
import com.dasolsystem.core.entity.Account;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.handler.EntityListBuilder;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.jparepository.AccountRepository;
import com.dasolsystem.core.elasticrepository.ElkAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final ElkAccountRepository elkAccountRepository;

    private final StringRedisTemplate redisTemplate;


    private static final String CACHE_PREFIX = "account:";
    private static final Long CACHE_EXPIRE = 60L;

    public ResponseJson<Object> findByMessage(String message){
        String cacheKey = CACHE_PREFIX + message;

        String cachedMessage = redisTemplate.opsForValue().get(cacheKey);
        if(cachedMessage != null){
            log.info("✅ Cached account message: {}", cachedMessage);
            return ResponseJson.builder()
                    .status(200)
                    .message("use cache")
                    .result(cachedMessage)
                    .build();
        }
        Account account = accountRepository.findByMessage(message);
        if(account != null){
            try{
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String json = mapper.writeValueAsString(account);
                redisTemplate.opsForValue().set(cacheKey, json, CACHE_EXPIRE, TimeUnit.SECONDS);
                log.info("✅ Cache saved message: {}", message);
                return ResponseJson.builder()
                        .status(200)
                        .message("use DB")
                        .result(json)
                        .build();
            }catch(JsonProcessingException e){
                log.error("JSON 변환 오류", e);
                throw new JsonFailException(ApiState.ERROR_1001,e.getMessage());
            }


        }
        return ResponseJson.builder()
                .status(404)
                .message("Not found")
                .build();
    }

    public List<AccountES> searchByMessage(String message){
        return elkAccountRepository.findByMessageContaining(message);
    }


}
