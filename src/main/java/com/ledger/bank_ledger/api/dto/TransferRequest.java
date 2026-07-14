package com.ledger.bank_ledger.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(UUID fromId, UUID toId, BigDecimal amount,String idempotencyKey) {

}