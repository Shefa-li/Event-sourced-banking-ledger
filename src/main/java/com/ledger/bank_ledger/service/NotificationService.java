package com.ledger.bank_ledger.service;


import com.ledger.bank_ledger.events.NotificationEvent;
import com.ledger.bank_ledger.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void sendCreditNotification(UUID accountId, BigDecimal amount) {
        notificationRepository.save(NotificationEvent.builder()
                .accountId(accountId)
                .type("CREDIT")
                .message("Amount ₹" + amount + " credited to your account")
                .createdAt(LocalDateTime.now())
                .build());
    }

    public void sendDebitNotification(UUID accountId, BigDecimal amount) {
        notificationRepository.save(NotificationEvent.builder()
                .accountId(accountId)
                .type("DEBIT")
                .message("Amount ₹" + amount + " debited from your account")
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<NotificationEvent> getNotifications(UUID accountId) {
        return notificationRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public List<NotificationEvent> getUnreadNotifications(UUID accountId) {
        return notificationRepository.findByAccountIdAndIsRead(accountId, false);
    }

    public void markAsRead(Long notificationId) {
        NotificationEvent notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notificationRepository.save(NotificationEvent.builder()
                .id(notification.getId())
                .accountId(notification.getAccountId())
                .type(notification.getType())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .isRead(true)
                .build());
    }
}
