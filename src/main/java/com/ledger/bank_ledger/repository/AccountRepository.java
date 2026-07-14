package com.ledger.bank_ledger.repository;

import com.ledger.bank_ledger.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByName(String name);
}