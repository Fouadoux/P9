package com.glucovion.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DisabledAccountException.class)
    public ResponseEntity<Map<String, String>> handleDisabledAccountException(DisabledAccountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("error", "DISABLED_ACCOUNT", "details", ex.getMessage())
        );
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "INVALID_CREDENTIALS", "details", ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException .class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException (IllegalArgumentException  ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error", "ILLEGAL_ARGUMENT", "details", ex.getMessage())
        );
    }

}

