package com.ledger.bank_ledger.service;

import com.ledger.bank_ledger.domain.Account;
import com.ledger.bank_ledger.domain.User;
import com.ledger.bank_ledger.events.MoneyMovedEvent;
import com.ledger.bank_ledger.repository.AccountRepository;
import com.ledger.bank_ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final JavaMailSender mailSender;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String from;

    public void sendTransactionEmail(MoneyMovedEvent e) {
        try {
            Account account = accountRepository.findById(e.accountId()).orElse(null);
            if (account == null || account.getUserId() == null) return;

            User user = userRepository.findById(account.getUserId()).orElse(null);
            if (user == null || user.getEmail() == null) return;   // no email on file

            boolean credit = "CREDIT".equals(e.type());
            String amount = money(e.amount());

            String headline = credit
                    ? "₹" + amount + " has been credited to your account."
                    : "₹" + amount + " has been debited from your account.";

            String detail = e.counterpartyName() == null
                    ? "Type: Cash deposit"
                    : (credit ? "Received from: " : "Sent to: ") + e.counterpartyName();

            String body = """
                Hi %s,

                %s

                %s
                Date & time: %s
                Available balance: ₹%s

                If you did not make this transaction, contact support immediately.
                """.formatted(account.getName(), headline, detail,
                    e.timestamp().format(FMT), money(e.balanceAfter()));

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(user.getEmail());
            mail.setSubject((credit ? "Credited ₹" : "Debited ₹") + amount);
            mail.setText(body);
            mailSender.send(mail);

        } catch (Exception ex) {
            log.error("Failed to send transaction email", ex);   // never break the transfer
        }
    }

    private String money(BigDecimal v) {
        return String.format("%,.2f", v);
    }
}
