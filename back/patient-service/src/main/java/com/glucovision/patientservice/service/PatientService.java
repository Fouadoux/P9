package com.glucovision.patientservice.service;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.exception.PatientNotFoundException;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    //retourne une liste de patient
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> getAllActivePatients() {
        return patientRepository.findByActiveTrue();
    }


    // Ajouter un patient
    public Patient addPatient(PatientDTO patientDTO) {

        Patient newPatient = new Patient();
        newPatient.setUid(UUID.randomUUID());
        newPatient.setFirstName(patientDTO.getFirstName());
        newPatient.setLastName(patientDTO.getLastName());
        newPatient.setBirthDate(patientDTO.getBirthDate());
        newPatient.setGender(patientDTO.getGender());
        newPatient.setAddress(patientDTO.getAddress());
        newPatient.setPhone(patientDTO.getPhone());
        newPatient.setActive(true);

        return patientRepository.save(newPatient);
    }


    // Trouver un patient par son nom
    public Patient findPatientByName(String lastName) {
        return patientRepository.findByLastName(lastName).orElseThrow(()->
                 new NoSuchElementException ("Patient with name " + lastName + " not found"));
    }

    // Mettre à jour un patient
    public PatientDTO updatePatient(UUID id, PatientDTO patientDTO) {
        return patientRepository.findByUid(id)
                .map(existingPatient -> {
                    updateFields(existingPatient, patientDTO);
                    Patient updatedPatient = patientRepository.save(existingPatient);
                    return convertToDTO(updatedPatient);
                })
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    private void updateFields(Patient patient, PatientDTO dto) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setPhone(dto.getPhone());
        patient.setActive(dto.isActive());
    }


    // Supprimer un patient
    public void deletePatient(UUID id) {
        if (!patientRepository.existsByUid(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteByUid(id);
    }


    public PatientDTO convertToDTO(Patient patient) {
        log.info("Converting user '{}' to DTO.", patient.getUid());

        PatientDTO dto = new PatientDTO();
        dto.setUid(patient.getUid());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setPhone(patient.getPhone());
        dto.setActive(patient.isActive());

        log.info("Patient '{}' converted to DTO successfully.", dto.getUid());
        return dto;
    }


    public List<PatientDTO> convertToDTOList(List<Patient> patients) {
        log.info("Converting list of patients to DTOs.");
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO toggleActivePatient(UUID id) {
        Patient patient = patientRepository.findByUid(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
        log.info("statu : {}", patient.isActive());
        patient.setActive(!patient.isActive());
        log.info("statu : {}", patient.isActive());
        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    public Patient findPatientById(UUID id) {
        return patientRepository.findByUid(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    public boolean ifActiveOrExistingPatient(UUID id) {
        Patient patient = findPatientById(id);
        return patient.isActive();
    }

}
