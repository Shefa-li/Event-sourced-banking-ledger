package com.ledger.bank_ledger.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//public interface LedgerEvent {
//    UUID getAccountId();
//    BigDecimal getAmount();
//    String getType(); // "CREDIT" or "DEBIT"
//    LocalDateTime getTimestamp();
//}

@Entity
@Table(name = "ledger_events")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(unique = true)
    private String idempotencyKey;  // to avoid duplicate transfer eg.user clicks twice


    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String type;        // "CREDIT" or "DEBIT"

    @Column(nullable = false)
    private LocalDateTime timestamp;
}