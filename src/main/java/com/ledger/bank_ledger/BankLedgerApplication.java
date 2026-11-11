package com.ledger.bank_ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BankLedgerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankLedgerApplication.class, args);
	}

}
