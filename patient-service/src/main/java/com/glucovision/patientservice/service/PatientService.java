package com.glucovision.patientservice.service;

import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    //retourne une liste de patient
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    // Ajouter un patient
    public Patient addPatient(Patient patient) {
        if (patient == null
                || patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()
                || patient.getLastName() == null || patient.getLastName().trim().isEmpty()
                || patient.getBirthDate() == null
                || patient.getGender() == null) {

            throw new IllegalArgumentException("Invalid patient data");
        }

        return patientRepository.save(patient);
    }

    // Trouver un patient par son ID
    public Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient with ID " + id + " not found"));
    }


    // Trouver un patient par son nom
    public Patient findPatientByName(String lastName) {
        return patientRepository.findByLastName(lastName).orElseThrow(()->
                 new NoSuchElementException ("Patient with name " + lastName + " not found"));
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
        if (!patientRepository.existsById(id)) {
            throw new NoSuchElementException("Patient with ID " + id + " not found");
        }
        patientRepository.deleteById(id);
    }
}
