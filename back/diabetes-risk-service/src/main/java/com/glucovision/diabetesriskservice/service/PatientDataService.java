package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.PatientClient;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientDataService {

    private final PatientClient patientClient;

    public PatientDto getPatient(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null ou vide");
        }
        return patientClient.getPatientById(id);
    }
}
