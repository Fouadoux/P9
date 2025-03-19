package com.glucovision.patientservice.service;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
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

    public PatientDTO convertToDTO(Patient patient) {
        log.info("Converting user '{}' to DTO.", patient.getId());

        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());

        log.info("Patient '{}' converted to DTO successfully.", patient.getId());
        return dto;
    }


    public List<PatientDTO> convertToDTOList(List<Patient> users) {
        log.info("Converting list of patients to DTOs.");
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



}
