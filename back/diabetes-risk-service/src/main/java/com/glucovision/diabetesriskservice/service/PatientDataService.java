package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.PatientClient;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for retrieving patient information by ID.
 * <p>
 * Communicates with the Patient Service through the {@link PatientClient}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientDataService {

    private final PatientClient patientClient;

    /**
     * Retrieves patient details for the given ID.
     *
     * @param id the unique identifier of the patient
     * @return a {@link PatientDto} containing patient information
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public PatientDto getPatient(String id) {
        log.debug("Fetching patient details for patientId={}", id);

        if (id == null || id.trim().isEmpty()) {
            log.warn("Invalid patient ID: null or empty");
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null ou vide");
        }

        PatientDto patient = patientClient.getPatientById(id);
        log.info("Patient data retrieved for patientId={}", id);
        return patient;
    }
}
