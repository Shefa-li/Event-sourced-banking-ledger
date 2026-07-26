package com.ledger.bank_ledger.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        //UUID fromId, UUID toId, BigDecimal amount,String idempotencyKey
        @NotNull(message = "fromId is required") UUID fromId,
        @NotNull(message = "toId is required") UUID toId,
        @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotBlank(message = "idempotencyKey is required") String idempotencyKey

) { }