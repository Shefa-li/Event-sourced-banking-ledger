package com.ledger.bank_ledger.api.dto;
import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(UUID accountId, BigDecimal amount) {}