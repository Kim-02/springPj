package com.dasolsystem.core.redis.reopsitory;

import com.dasolsystem.core.entity.RedisJwtId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisJwtRepository extends CrudRepository<RedisJwtId,Long> {
}
