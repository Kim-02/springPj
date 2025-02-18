package com.dasolsystem.add;

import com.dasolsystem.core.entity.Account;
import com.dasolsystem.core.tests.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
public class accountadd {

    @Autowired
    private AccountRepository accountRepository;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        System.out.println("✅ 모든 데이터 삭제");
    }

    @Test
    void insertTestCase(){
        IntStream.range(1,101).forEach(i ->{
                    Account account = Account.builder()
                            .name("User_"+i)
                            .balance(random.nextLong(1000,10000))
                            .message("Test account #"+i)
                            .updated_at(LocalDateTime.now())
                            .build();
                    accountRepository.save(account);
                }
                );
        System.out.println("✅ 500개의 테스트 데이터가 삽입되었습니다");
    }
}
