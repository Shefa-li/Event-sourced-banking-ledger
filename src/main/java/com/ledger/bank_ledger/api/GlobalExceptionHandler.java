package com.ledger.bank_ledger.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
//        return ResponseEntity.badRequest().body(ex.getMessage());
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
//        return ResponseEntity.status(409).body(ex.getMessage());
//    }
//}
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}