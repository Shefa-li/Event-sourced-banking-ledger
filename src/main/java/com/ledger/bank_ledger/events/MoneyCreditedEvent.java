package com.ledger.bank_ledger.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MoneyCreditedEvent extends LedgerEvent{

    private UUID accountId;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    @Override
    public String getType() { return "CREDIT"; }

    //@Override
    //public String getType() {return "DEBIT";}
}
