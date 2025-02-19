package com.dasolsystem.core.jparepository;


import com.dasolsystem.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByMessage(String message);
}
