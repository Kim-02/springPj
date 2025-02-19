package com.dasolsystem.core.elasticrepository;

import com.dasolsystem.core.document.AccountES;
import com.dasolsystem.core.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElkAccountRepository extends ElasticsearchRepository<AccountES,Long> {
    List<AccountES> findByMessageContaining(String message);

    @Query("{\"match_phrase\": {\"message\": \"?0\"}}")
    List<AccountES> findByMessagePhrase(String message);
}
