package com.ledger.bank_ledger.api;

import com.ledger.bank_ledger.api.dto.CreateAccountRequest;
import com.ledger.bank_ledger.api.dto.DepositRequest;
import com.ledger.bank_ledger.api.dto.PageResponse;
import com.ledger.bank_ledger.api.dto.TransferRequest;
import com.ledger.bank_ledger.domain.Account;
import com.ledger.bank_ledger.events.LedgerEvent;
import com.ledger.bank_ledger.repository.LedgerEventRepository;
import com.ledger.bank_ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Ledger", description = "Banking operations")
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    private final LedgerEventRepository eventRepository;

    public LedgerController(LedgerService ledgerService, LedgerEventRepository eventRepository) {
        this.ledgerService = ledgerService;
        this.eventRepository = eventRepository;
    }

    // Deposit money into an account
    @Operation(summary = "Deposit money into account")
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@Valid @RequestBody DepositRequest request) {
        ledgerService.deposit(request.accountId(), request.amount());
        return ResponseEntity.ok("Deposit successful");
    }

    // Transfer money between two accounts
    @Operation(summary = "Transfer money between accounts")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {
        ledgerService.transfer(request.fromId(), request.toId(), request.amount(), request.idempotencyKey());
        return ResponseEntity.ok("Transfer successful");
    }

    // Get balance for an account
    @Operation(summary = "Get account balance")
    @GetMapping("/balance/{accountId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID accountId) {
        return ResponseEntity.ok(ledgerService.getBalance(accountId));
    }

    @Operation(summary = "Deposit money into account")
    @PostMapping("/account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.ok(ledgerService.createAccount(request.name()));
    }


//    @Operation(summary = "Get transaction history")
//    @GetMapping("/history/{accountId}")
//    public ResponseEntity<List<LedgerEvent>> getHistory(@PathVariable UUID accountId) {
//        return ResponseEntity.ok(eventRepository.findByAccountId(accountId));
//    }

    @Operation(summary = "Get paginated transaction history")
    @GetMapping("/history/{accountId}")
    public ResponseEntity<PageResponse<LedgerEvent>> getHistory(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LedgerEvent> result = eventRepository.findByAccountId(
                accountId,
                PageRequest.of(page, size, Sort.by("timestamp").descending()));

        return ResponseEntity.ok(new PageResponse<>(
                result.getContent(),       // not result.content()
                result.getNumber(),        // current page number
                result.getSize(),          // page size
                result.getTotalElements(), // total records
                result.getTotalPages()     // total pages
        ));
    }


    @Operation(summary = "Deactivate an account")
    @PutMapping("/account/{accountId}/deactivate")
    public ResponseEntity<Account> deactivate(@PathVariable UUID accountId) {
        return ResponseEntity.ok(ledgerService.deactivateAccount(accountId));
    }

    @Operation(summary = "Activate an account")
    @PutMapping("/account/{accountId}/activate")
    public ResponseEntity<Account> activate(@PathVariable UUID accountId) {
        return ResponseEntity.ok(ledgerService.activateAccount(accountId));
    }
}