package com.ledger.bank_ledger.repository;


import com.ledger.bank_ledger.events.LedgerEvent;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class EventStore {

    private final List<LedgerEvent> events = new ArrayList<>();

    public void append(LedgerEvent event) {
        events.add(event);          // only add, never update/delete
    }

    public List<LedgerEvent> getEventsForAccount(UUID accountId) {
        return events.stream()
                .filter(e -> e.getAccountId().equals(accountId))
                .toList();
    }
}
