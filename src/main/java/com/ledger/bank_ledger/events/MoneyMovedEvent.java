package com.ledger.bank_ledger.events;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MoneyMovedEvent (
            UUID accountId,
            String type,                // CREDIT or DEBIT
            BigDecimal amount,
            String counterpartyName,    // null for deposits
            BigDecimal balanceAfter,
            LocalDateTime timestamp
    ) {}