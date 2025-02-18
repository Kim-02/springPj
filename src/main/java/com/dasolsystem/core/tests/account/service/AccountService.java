package com.dasolsystem.core.tests.account.service;

import com.dasolsystem.core.entity.Account;
import com.dasolsystem.core.tests.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "account:";
    private static final Long CACHE_EXPIRE = 60L;

    public String getMessageByName(String name){
        String cacheKey = CACHE_PREFIX + name;

        String cachedMessage = redisTemplate.opsForValue().get(cacheKey);
        if(cachedMessage != null){
            log.info("✅ Cached account message: {}", cachedMessage);
            return cachedMessage;
        }
        Optional<Account> account = accountRepository.findByName(name);
        if(account.isPresent()){
            Account ac = account.get();
            redisTemplate.opsForValue().set(cacheKey,ac.getMessage(),CACHE_EXPIRE, TimeUnit.SECONDS);
            log.info("✅ Cache saved message: {}", ac);

            return ac.getMessage();
        }
        return null;
    }

}
