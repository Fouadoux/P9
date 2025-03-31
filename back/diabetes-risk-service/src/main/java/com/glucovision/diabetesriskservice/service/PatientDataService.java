package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.PatientClient;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientDataService {
    private final PatientClient patientClient;

    public PatientDto getPatient(Long id) {
        return patientClient.getPatientById(id);
    }
}
