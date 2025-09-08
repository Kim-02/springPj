package com.dasolsystem.core.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RedisJwtId")
public class RedisJwtId {

    @Id
    private Long id;

    @Indexed
    private String jti;

    private String jwtToken;

    @TimeToLive
    private Long ttl;

}
