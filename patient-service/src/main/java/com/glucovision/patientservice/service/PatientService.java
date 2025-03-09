package com.glucovision.patientservice.service;

import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Ajouter un patient
    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Trouver un patient par son ID
    public Optional<Patient> findPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Trouver un patient par son nom
    public Patient findPatientByName(String lastName) {
        return patientRepository.findByLastName(lastName);
    }

    // Mettre à jour un patient
    public Patient updatePatient(Long id, Patient patientDetails) {
        if (patientRepository.existsById(id)) {
            patientDetails.setId(id);
            return patientRepository.save(patientDetails);
        }
        return null;
    }

    // Supprimer un patient
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
