# Event-Sourced Banking Ledger

A Spring Boot backend application implementing banking ledger operations using event-based transaction storage.

## Features

- Account Creation
- Deposit Money
- Fund Transfer
- Balance Projection
- Transaction History
- Idempotency Handling
- Insufficient Funds Validation
- Pagination
- Swagger API Documentation

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger/OpenAPI

## API Endpoints

### Create Account

POST /api/ledger/account

### Deposit

POST /api/ledger/deposit

### Transfer

POST /api/ledger/transfer

### Get Balance

GET /api/ledger/balance/{accountId}

### Transaction History

GET /api/ledger/history/{accountId}

## Concepts Implemented

- Event Sourcing
- Transaction Management
- Idempotency
- REST APIs
- Pagination
