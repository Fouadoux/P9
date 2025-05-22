package com.glucovision.noteservice.service;

import com.glucovision.noteservice.client.PatientClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for checking patient-related information via the Patient microservice.
 * <p>
 * Currently, it delegates the verification of a patient's active status to the {@link PatientClient}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PatientDataService {

    private final PatientClient patientClient;

    /**
     * Checks whether a patient is active by delegating to the Patient microservice.
     *
     * @param id the unique identifier of the patient
     * @return true if the patient is active, false otherwise
     */
    public boolean isActivePatient(String id) {
        log.info("[PATIENT CHECK] Verifying active status for patientId={}", id);
        boolean result = patientClient.isActivePatient(id);
        log.info("[PATIENT CHECK] Result for patientId={} => active={}", id, result);
        return result;
    }
}
