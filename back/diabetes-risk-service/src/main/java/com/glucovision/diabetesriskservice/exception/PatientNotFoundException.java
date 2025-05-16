package com.glucovision.diabetesriskservice.exception;

/**
 * Exception thrown when a patient cannot be found by the provided identifier.
 * <p>
 * Typically used in services to signal that a patient does not exist
 * in the system or is inactive.
 */
public class PatientNotFoundException extends RuntimeException {

    /**
     * Constructs a new PatientNotFoundException with a specific message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public PatientNotFoundException(String message) {
        super(message);
    }
}
