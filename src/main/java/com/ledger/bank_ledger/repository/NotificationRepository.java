package com.ledger.bank_ledger.repository;

import com.ledger.bank_ledger.domain.Notification;
import com.ledger.bank_ledger.events.LedgerEvent;
import com.ledger.bank_ledger.events.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
    List<Notification> findByAccountIdAndIsRead(UUID accountId, boolean isRead);
}
