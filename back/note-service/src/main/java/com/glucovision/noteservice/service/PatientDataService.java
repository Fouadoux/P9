package com.glucovision.noteservice.service;

import com.glucovision.noteservice.client.PatientClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PatientDataService {

    private final PatientClient patientClient;

    public boolean isActivePatient (String id){return  patientClient.isActivePatient(id);}
}
