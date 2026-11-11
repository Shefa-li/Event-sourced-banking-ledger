package com.ledger.bank_ledger.service;

import com.ledger.bank_ledger.events.MoneyMovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransactionEmailListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener   // fires only after the transaction commits
    public void onMoneyMoved(MoneyMovedEvent event) {
        emailService.sendTransactionEmail(event);
    }
}
