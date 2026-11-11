package com.ledger.bank_ledger.service;

import com.ledger.bank_ledger.domain.Account;
import com.ledger.bank_ledger.events.LedgerEvent;
import com.ledger.bank_ledger.events.MoneyMovedEvent;
import com.ledger.bank_ledger.repository.AccountRepository;
import com.ledger.bank_ledger.repository.LedgerEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor   // no manual constructor needed
public class LedgerService {

    private final LedgerEventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final AccountProjection projection;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher publisher;

    public Optional<Account> findAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No account found for this user"));
    }

    public Account createAccount(String name, Long userId) {
        if (accountRepository.findByUserId(userId).isPresent())
            throw new IllegalStateException("You already have an account");
        if (accountRepository.findByName(name).isPresent())
            throw new IllegalArgumentException("Account name already taken");

        return accountRepository.save(Account.builder().name(name).userId(userId).build());
    }

    // ---------- Deposit ----------
    @Transactional
    public void deposit(UUID accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!account.isActive())
            throw new IllegalStateException("Account is inactive");

        LocalDateTime now = LocalDateTime.now();

        eventRepository.save(LedgerEvent.builder()
                .accountId(accountId)
                .amount(amount)
                .type("CREDIT")
                .timestamp(now)
                .build());

        BigDecimal balance = projection.getBalance(accountId);

        notificationService.notify(accountId, "CREDIT", amount, null, balance);
        publisher.publishEvent(new MoneyMovedEvent(accountId, "CREDIT", amount, null, balance, now));
    }

    // ---------- Transfer ----------
    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount, String idempotencyKey) {

        if (fromId.equals(toId))
            throw new IllegalArgumentException("Cannot transfer to your own account");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        if (idempotencyKey != null && eventRepository.existsByIdempotencyKey(idempotencyKey))
            throw new IllegalStateException("Duplicate transaction detected");

        Account sender = accountRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
        if (!sender.isActive())
            throw new IllegalStateException("Sender account is inactive");

        Account receiver = accountRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
        if (!receiver.isActive())
            throw new IllegalStateException("Receiver account is inactive");

        if (projection.getBalance(fromId).compareTo(amount) < 0)
            throw new IllegalStateException("Insufficient funds");

        LocalDateTime now = LocalDateTime.now();

        eventRepository.save(LedgerEvent.builder()
                .accountId(fromId).amount(amount).type("DEBIT")
                .idempotencyKey(idempotencyKey).timestamp(now).build());

        eventRepository.save(LedgerEvent.builder()
                .accountId(toId).amount(amount).type("CREDIT")
                .idempotencyKey(idempotencyKey + "_credit").timestamp(now).build());

        // balances AFTER the transfer
        BigDecimal senderBalance = projection.getBalance(fromId);
        BigDecimal receiverBalance = projection.getBalance(toId);

        notificationService.notify(fromId, "DEBIT",  amount, receiver.getName(), senderBalance);
        notificationService.notify(toId,   "CREDIT", amount, sender.getName(),   receiverBalance);

        publisher.publishEvent(new MoneyMovedEvent(fromId, "DEBIT",  amount, receiver.getName(), senderBalance,   now));
        publisher.publishEvent(new MoneyMovedEvent(toId,   "CREDIT", amount, sender.getName(),   receiverBalance, now));
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