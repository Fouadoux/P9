package com.glucovision.noteservice.exception;

/**
 * Exception thrown when a patient cannot be found or is inactive.
 */
public class PatientNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a message referencing the missing patient's UID.
     *
     * @param patientId the UID of the patient that was not found
     */
    public PatientNotFoundException(String patientId) {
        super(String.format("Patient with ID '%s' does not exist or is inactive.", patientId));
    }
}

