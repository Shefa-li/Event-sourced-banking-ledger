package com.ledger.bank_ledger.api;

import com.ledger.bank_ledger.events.NotificationEvent;
import com.ledger.bank_ledger.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications for account")
    @GetMapping("/{accountId}")
    public ResponseEntity<List<NotificationEvent>> getAll(@PathVariable UUID accountId) {
        return ResponseEntity.ok(notificationService.getNotifications(accountId));
    }

    @Operation(summary = "Get unread notifications")
    @GetMapping("/{accountId}/unread")
    public ResponseEntity<List<NotificationEvent>> getUnread(@PathVariable UUID accountId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(accountId));
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Marked as read");
    }
}
