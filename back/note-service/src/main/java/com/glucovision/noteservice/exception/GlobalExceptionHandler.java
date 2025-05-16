package com.glucovision.noteservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
/**
 * Global exception handler for the Note Service.
 * <p>
 * This class handles various types of exceptions and returns standardized HTTP responses.
 * It ensures that API consumers receive meaningful error messages with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles forbidden operations, typically caused by business logic violations.
     *
     * @param ex the exception thrown
     * @return a 403 FORBIDDEN response with a descriptive error message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles validation errors on request body parameters (e.g. @Valid).
     *
     * @param ex the exception thrown when validation fails
     * @return a 400 BAD REQUEST response with field-specific error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles cases where a requested element is not found in the system.
     *
     * @param ex the exception indicating a missing resource
     * @return a 404 NOT FOUND response with an error message
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles custom {@link PatientNotFoundException} for missing patients.
     *
     * @param ex the custom exception
     * @return a 404 NOT FOUND response with a specific error message
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(PatientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles all unexpected exceptions not explicitly caught by other handlers.
     *
     * @param ex the unexpected exception
     * @return a 500 INTERNAL SERVER ERROR response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Une erreur est survenue : " + ex.getMessage()));
    }
}
