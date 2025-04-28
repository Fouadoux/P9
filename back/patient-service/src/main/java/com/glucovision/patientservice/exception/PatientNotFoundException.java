package com.glucovision.patientservice.exception;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(UUID patientId) {
        super("Le patient avec l'id " + patientId + " est inexistant ou inactif.");    }
}
