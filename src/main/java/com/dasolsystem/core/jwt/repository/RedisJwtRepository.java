package com.dasolsystem.core.jwt.repository;

import com.dasolsystem.core.entity.RedisJwtId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisJwtRepository extends CrudRepository<RedisJwtId,Long> {
}
