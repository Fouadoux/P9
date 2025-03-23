package com.glucovision.patientservice.service;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Patient addPatient(PatientDTO patientDTO) {
        if (patientDTO == null
                || patientDTO.getFirstName() == null || patientDTO.getFirstName().trim().isEmpty()
                || patientDTO.getLastName() == null || patientDTO.getLastName().trim().isEmpty()
                || patientDTO.getBirthDate() == null
                || patientDTO.getGender() == null) {

            throw new IllegalArgumentException("Invalid patient data");
        }
        Patient newPatient = new Patient();
        newPatient.setFirstName(patientDTO.getFirstName());
        newPatient.setLastName(patientDTO.getLastName());
        newPatient.setBirthDate(patientDTO.getBirthDate());
        newPatient.setGender(patientDTO.getGender());
        newPatient.setAddress(patientDTO.getAddress());
        newPatient.setPhoneNumber(patientDTO.getPhoneNumber());

        return patientRepository.save(newPatient);
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
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        return patientRepository.findById(id).map(existingPatient -> {
            existingPatient.setFirstName(patientDTO.getFirstName());
            existingPatient.setLastName(patientDTO.getLastName());
            existingPatient.setBirthDate(patientDTO.getBirthDate());
            existingPatient.setGender(patientDTO.getGender());
            existingPatient.setAddress(patientDTO.getAddress());
            existingPatient.setPhoneNumber(patientDTO.getPhoneNumber());

            // Sauvegarde le patient et convertit en DTO
            Patient updatedPatient = patientRepository.save(existingPatient);
            return convertToDTO(updatedPatient);
        }).orElse(null);
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
        dto.setAddress(patient.getAddress());
        dto.setPhoneNumber(patient.getPhoneNumber());

        log.info("Patient '{}' converted to DTO successfully.", patient.getId());
        return dto;
    }


    public List<PatientDTO> convertToDTOList(List<Patient> patients) {
        log.info("Converting list of patients to DTOs.");
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



}
