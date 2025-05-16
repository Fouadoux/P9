package com.glucovision.patientservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for the Patient Service.
 * <p>
 * Handles common exceptions such as validation errors and entity not found.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles generic not found exceptions.
     *
     * @param ex the exception thrown
     * @return a 404 Not Found response
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFoundException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles validation errors from @Valid annotated inputs.
     *
     * @param ex the exception thrown
     * @return a 400 Bad Request response with field-specific error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("errors", fieldErrors);
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles patient not found scenarios.
     *
     * @param ex the exception thrown
     * @return a 404 Not Found response with message
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<String> handlePatientNotFound(PatientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles duplicate patient registration attempts.
     *
     * @param ex the exception thrown
     * @return a 409 Conflict response
     */
    @ExceptionHandler(DuplicatePatientException.class)
    public ResponseEntity<String> handleDuplicatePatientException(DuplicatePatientException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
