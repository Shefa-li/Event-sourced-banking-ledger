package com.ledger.bank_ledger.service;

import com.ledger.bank_ledger.repository.EventStore;
import com.ledger.bank_ledger.repository.LedgerEventRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountProjection {

    private final LedgerEventRepository eventRepository;

    public AccountProjection(LedgerEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public BigDecimal getBalance(UUID accountId) {
        return eventRepository.findByAccountId(accountId)
                .stream()
                .map(e -> e.getType().equals("CREDIT")
                        ? e.getAmount()
                        : e.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}