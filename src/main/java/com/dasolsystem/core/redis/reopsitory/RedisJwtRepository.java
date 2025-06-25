package com.dasolsystem.core.redis.reopsitory;

import com.dasolsystem.core.entity.RedisJwtId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisJwtRepository extends CrudRepository<RedisJwtId,Long> {
    Optional<RedisJwtId> findByJti(String jti);
}
