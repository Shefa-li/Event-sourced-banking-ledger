package com.ledger.bank_ledger.repository;

import com.ledger.bank_ledger.api.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.ledger.bank_ledger.events.LedgerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LedgerEventRepository extends JpaRepository<LedgerEvent, UUID> {

    boolean existsByIdempotencyKey(String idempotencyKey);// for twice transfer
    List<LedgerEvent> findByAccountId(UUID accountId);

    //PageResponse<LedgerEvent> findByAccountId(UUID accountId, PageRequest pageRequest);
    Page<LedgerEvent> findByAccountId(UUID accountId, Pageable pageable);// for pagination
}