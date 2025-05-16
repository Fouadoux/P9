package com.glucovision.diabetesriskservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * Global exception handler for the Diabetes Risk Service.
 * <p>
 * Captures and handles common exceptions thrown by the application
 * and returns consistent error responses to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link IllegalStateException}, typically thrown when access is denied or an operation is invalid.
     *
     * @param ex the exception thrown
     * @return a 403 Forbidden response with the exception message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles validation errors for invalid method arguments annotated with {@code @Valid}.
     * <p>
     * Aggregates all field-specific validation errors into a single message.
     *
     * @param ex the exception containing validation error details
     * @return a 400 Bad Request response with formatted validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(sb.toString()));
    }

    /**
     * Handles {@link NoSuchElementException}, commonly thrown when a requested entity is not found.
     *
     * @param ex the exception thrown
     * @return a 404 Not Found response with the exception message
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles uncaught generic exceptions.
     *
     * @param ex the unexpected exception
     * @return a 500 Internal Server Error response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Une erreur est survenue : " + ex.getMessage()));
    }

    /**
     * Handles custom {@link PatientNotFoundException} when a patient cannot be located in the system.
     *
     * @param ex the exception thrown
     * @return a 404 Not Found response with the specific error message
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(PatientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
