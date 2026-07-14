package com.ledger.bank_ledger.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";   // ACTIVE or INACTIVE

    public void deactivate() { this.status = "INACTIVE"; }
    public void activate()   { this.status = "ACTIVE"; }
    public boolean isActive(){ return "ACTIVE".equals(this.status); }
}