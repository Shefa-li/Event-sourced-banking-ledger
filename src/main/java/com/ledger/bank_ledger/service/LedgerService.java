package com.ledger.bank_ledger.service;

import com.ledger.bank_ledger.domain.Account;
import com.ledger.bank_ledger.events.LedgerEvent;
import com.ledger.bank_ledger.repository.AccountRepository;
import com.ledger.bank_ledger.repository.LedgerEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//@Service
//public class LedgerService {
//
//    private final EventStore eventStore;
//    private final AccountProjection projection;
//
//    public LedgerService(EventStore eventStore, AccountProjection projection) {
//        this.eventStore = eventStore;
//        this.projection = projection;
//    }
//
//    public void deposit(UUID accountId, BigDecimal amount) {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0)
//            throw new IllegalArgumentException("Amount must be positive");
//
//        eventStore.append(new MoneyCreditedEvent(accountId, amount, LocalDateTime.now()));
//    }
//
//    public void transfer(UUID fromId, UUID toId, BigDecimal amount) {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0)
//            throw new IllegalArgumentException("Amount must be positive");
//
//        BigDecimal senderBalance = projection.getBalance(fromId);
//        if (senderBalance.compareTo(amount) < 0)
//            throw new IllegalStateException("Insufficient funds");
//
//        eventStore.append(new MoneyDebitedEvent(fromId, amount, LocalDateTime.now()));
//        eventStore.append(new MoneyCreditedEvent(toId, amount, LocalDateTime.now()));
//    }
//
//    public BigDecimal getBalance(UUID accountId) {
//        return projection.getBalance(accountId);
//    }
//}

@Service
public class LedgerService {

    private final LedgerEventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final AccountProjection projection;

    public LedgerService(LedgerEventRepository eventRepository,
                         AccountRepository accountRepository,
                         AccountProjection projection) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.projection = projection;
    }

    // Create account
    public Account createAccount(String name) {
        if (accountRepository.findByName(name).isPresent())
            throw new IllegalArgumentException("Account already exists: " + name);

        return accountRepository.save(Account.builder().name(name).build());
    }

    public Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No account found for this user"));
    }

    // Deposit
    @Transactional
    public void deposit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!account.isActive())
            throw new IllegalStateException("Account is inactive");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        eventRepository.save(LedgerEvent.builder()
                .accountId(accountId)
                .amount(amount)
                .type("CREDIT")
                .timestamp(LocalDateTime.now())
                .build());
    }

    // Transfer
    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount, String idempotencyKey) {

        Account sender = accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        if (!sender.isActive())
            throw new IllegalStateException("Sender account is inactive");

        Account receiver = accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        if (!receiver.isActive())
            throw new IllegalStateException("Receiver account is inactive");

        if (idempotencyKey != null && eventRepository.existsByIdempotencyKey(idempotencyKey))
            throw new IllegalStateException("Duplicate transaction detected");

        System.out.println("Idempotency Key received: " + idempotencyKey);

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
        accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        if (projection.getBalance(fromId).compareTo(amount) < 0)
            throw new IllegalStateException("Insufficient funds");

        eventRepository.save(LedgerEvent.builder()
                .accountId(fromId).amount(amount).type("DEBIT").idempotencyKey(idempotencyKey).timestamp(LocalDateTime.now()).build());

        eventRepository.save(LedgerEvent.builder()
                .accountId(toId).amount(amount).type("CREDIT").idempotencyKey(idempotencyKey + "_credit").timestamp(LocalDateTime.now()).build());

    }
    public Account deactivateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.deactivate();
        return accountRepository.save(account);
    }

    public Account activateAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.activate();
        return accountRepository.save(account);
    }

    public BigDecimal getBalance(UUID accountId) {
        return projection.getBalance(accountId);
    }
}