package com.glucovision.patientservice.exception;

/**
 * Thrown when attempting to create a patient that already exists
 * based on unique identity constraints.
 */
public class DuplicatePatientException extends RuntimeException {
    public DuplicatePatientException(String message) {
        super(message);
    }
}