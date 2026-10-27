package com.ledger.bank_ledger.repository;

import com.ledger.bank_ledger.events.LedgerEvent;
import com.ledger.bank_ledger.events.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface NotificationRepository extends JpaRepository<NotificationEvent, Long> {
    List<NotificationEvent> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
    List<NotificationEvent> findByAccountIdAndIsRead(UUID accountId, boolean isRead);
}
