package com.glucovision.patientservice.service;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.exception.DuplicatePatientException;
import com.glucovision.patientservice.exception.PatientNotFoundException;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing {@link Patient} entities.
 * <p>
 * Provides business logic for creating, updating, retrieving, and deleting patients.
 */
@Service
@Slf4j
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Retrieves all patients sorted by last name and first name.
     *
     * @return a sorted list of all patients
     */
    public List<Patient> getAllPatients() {
        log.debug("Fetching all patients (sorted by last name, first name).");
        return patientRepository.findAll(Sort.by("lastName").and(Sort.by("firstName")));
    }

    /**
     * Retrieves all active patients sorted by last name and first name.
     *
     * @return a sorted list of active patients
     */
    public List<Patient> getAllActivePatients() {
        log.debug("Fetching all active patients (sorted by last name, first name).");
        return patientRepository.findByActiveTrue(Sort.by("lastName").and(Sort.by("firstName")));
    }

    public Page<PatientDTO> getAllActivePatientsPage(Pageable pageable) {
        log.debug("Fetching all active patients (sorted by last name, first name).");
        Page<Patient> activePatients = patientRepository.findAll(pageable);
        List<PatientDTO> dtoList = convertToDTOList(activePatients.getContent());
        return new PageImpl<>(dtoList, pageable, activePatients.getTotalElements());
    }


    public Page<PatientDTO> getAllActivePatientsPaginated(Pageable pageable) {
        log.debug("Fetching all active patients (sorted by last name, first name).");
        Page<Patient> activePatients = patientRepository.findByActiveTrue(pageable);
        List<PatientDTO> dtoList = convertToDTOList(activePatients.getContent());
        return new PageImpl<>(dtoList, pageable, activePatients.getTotalElements());
    }



    /**
     * Adds a new patient to the system.
     *
     * @param patientDTO the data to create the patient
     * @return the created {@link Patient}
     * @throws DuplicatePatientException if a patient with the same identity already exists
     */
    public Patient addPatient(PatientDTO patientDTO) throws DuplicatePatientException {
        log.info("Attempting to add new patient: {} {}", patientDTO.getFirstName(), patientDTO.getLastName());

        boolean alreadyExists = patientRepository.existsByFirstNameAndLastNameAndBirthDate(
                patientDTO.getFirstName(),
                patientDTO.getLastName(),
                patientDTO.getBirthDate()
        );

        if (alreadyExists) {
            log.warn("Duplicate patient detected: {} {} {}", patientDTO.getFirstName(), patientDTO.getLastName(), patientDTO.getBirthDate());
            throw new DuplicatePatientException("This patient already exists in the system.");
        }

        Patient newPatient = new Patient();
        newPatient.setUid(UUID.randomUUID().toString());
        newPatient.setFirstName(patientDTO.getFirstName());
        newPatient.setLastName(patientDTO.getLastName());
        newPatient.setBirthDate(patientDTO.getBirthDate());
        newPatient.setGender(patientDTO.getGender());
        newPatient.setAddress(patientDTO.getAddress());
        newPatient.setPhone(patientDTO.getPhone());
        newPatient.setActive(true);

        Patient saved = patientRepository.save(newPatient);
        log.info("Patient created successfully with UID: {}", saved.getUid());
        return saved;
    }

    /**
     * Finds a patient by their last name.
     *
     * @param lastName the last name of the patient
     * @return the matching {@link Patient}
     * @throws PatientNotFoundException if no patient is found
     */
    public Patient findPatientActiveByName(String lastName) {
        log.debug("Searching for patient by last name: {}", lastName);
        return patientRepository.findByLastName(lastName)
                .orElseThrow(() -> new PatientNotFoundException("Patient with name " + lastName + " not found"));
    }

    public Page<PatientDTO> findPatientActiveByNamePaginated(String lastName, Pageable pageable) {
        log.debug("Searching for active patients by last name (partial match): {}", lastName);
        Page<Patient> patients = patientRepository.findByActiveTrueAndLastNameContainingIgnoreCase(lastName, pageable);
        List<PatientDTO> dtoList = convertToDTOList(patients.getContent());
        return new PageImpl<>(dtoList, pageable, patients.getTotalElements());
    }

    public Page<PatientDTO> searchPatientsPaginated(String lastName, Pageable pageable) {
        log.debug("Searching for active patients by last name (partial match): {}", lastName);
        Page<Patient> patients = patientRepository.findByLastNameContainingIgnoreCase(lastName, pageable);
        List<PatientDTO> dtoList = convertToDTOList(patients.getContent());
        return new PageImpl<>(dtoList, pageable, patients.getTotalElements());
    }

    /**
     * Updates an existing patient by UID.
     *
     * @param uid the UID of the patient to update
     * @param patientDTO the new data
     * @return the updated {@link PatientDTO}
     * @throws PatientNotFoundException if no patient is found
     */
    public PatientDTO updatePatient(String uid, PatientDTO patientDTO) {
        log.info("Updating patient with UID: {}", uid);
        return patientRepository.findByUid(uid)
                .map(existingPatient -> {
                    updateFields(existingPatient, patientDTO);
                    Patient updatedPatient = patientRepository.save(existingPatient);
                    log.info("Patient with UID {} updated successfully.", uid);
                    return convertToDTO(updatedPatient);
                })
                .orElseThrow(() -> new PatientNotFoundException(uid));
    }

    /**
     * Updates fields of an existing patient with values from a DTO.
     *
     * @param patient the existing patient entity
     * @param dto the new data to apply
     */
    private void updateFields(Patient patient, PatientDTO dto) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setPhone(dto.getPhone());
        patient.setActive(dto.getActive());
    }

    /**
     * Deletes a patient by UID.
     *
     * @param uid the UID of the patient to delete
     * @throws PatientNotFoundException if the patient does not exist
     */
    public void deletePatient(String uid) {
        log.info("Deleting patient with UID: {}", uid);
        if (!patientRepository.existsByUid(uid)) {
            throw new PatientNotFoundException(uid);
        }
        patientRepository.deleteByUid(uid);
        log.info("Patient with UID {} deleted successfully.", uid);
    }

    /**
     * Converts a {@link Patient} entity to a {@link PatientDTO}.
     *
     * @param patient the patient entity
     * @return the corresponding DTO
     */
    public PatientDTO convertToDTO(Patient patient) {
        log.debug("Converting patient '{}' to DTO.", patient.getUid());

        PatientDTO dto = new PatientDTO();
        dto.setUid(patient.getUid());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setPhone(patient.getPhone());
        dto.setActive(patient.getActive());

        log.debug("Patient '{}' converted to DTO successfully.", dto.getUid());
        return dto;
    }

    /**
     * Converts a list of patients to a list of DTOs.
     *
     * @param patients list of entities
     * @return list of corresponding DTOs
     */
    public List<PatientDTO> convertToDTOList(List<Patient> patients) {
        log.debug("Converting list of patients to DTOs. Count: {}", patients.size());
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Toggles the active status of a patient.
     *
     * @param uid the UID of the patient
     * @return the updated patient DTO
     * @throws PatientNotFoundException if no patient is found
     */
    public PatientDTO toggleActivePatient(String uid) {
        log.info("Toggling active status for patient with UID: {}", uid);
        Patient patient = patientRepository.findByUid(uid)
                .orElseThrow(() -> new PatientNotFoundException(uid));
        patient.setActive(!patient.getActive());
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient UID {} is now active = {}", uid, savedPatient.getActive());
        return convertToDTO(savedPatient);
    }

    /**
     * Finds a patient by their UID.
     *
     * @param uid the unique identifier
     * @return the matching {@link Patient}
     * @throws PatientNotFoundException if no patient is found
     */
    public Patient findPatientById(String uid) {
        log.debug("Searching for patient by UID: {}", uid);
        return patientRepository.findByUid(uid)
                .orElseThrow(() -> new PatientNotFoundException(uid));
    }

    /**
     * Checks if a patient is active based on their UID.
     *
     * @param uid the unique identifier
     * @return {@code true} if the patient is active
     * @throws PatientNotFoundException if the patient is not found
     */
    public boolean isActive(String uid) {
        log.debug("Checking active status for patient UID: {}", uid);
        return findPatientById(uid).getActive();
    }
}
