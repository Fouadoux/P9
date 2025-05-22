package com.glucovision.patientservice.controller;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.exception.DuplicatePatientException;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing patients.
 * <p>
 * Provides endpoints to create, read, update, toggle, and delete patients.
 * Also includes routes for checking active status and searching by name.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Endpoints for managing patient data")
public class PatientController {

    private final PatientService patientService;

    /**
     * Creates a new patient.
     *
     * @param patient the patient data
     * @return the created patient with UID
     * @throws DuplicatePatientException if a duplicate patient exists
     */
    @Operation(summary = "Create a new patient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patient) throws DuplicatePatientException {
        log.info("[POST] Creating new patient: {}", patient);
        Patient savedPatient = patientService.addPatient(patient);
        log.info("✅ Patient created with UID: {}", savedPatient.getUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    /**
     * Retrieves a patient by UID.
     *
     * @param uid the unique identifier
     * @return the patient DTO
     */
    @Operation(summary = "Get a patient by UID")
    @GetMapping("/{uid}")
    public ResponseEntity<PatientDTO> getPatientById(
            @Parameter(description = "Patient UID") @PathVariable String uid) {
        log.info("[GET] Retrieving patient by UID: {}", uid);
        Patient patient = patientService.findPatientById(uid);
        PatientDTO patientDto = patientService.convertToDTO(patient);
        log.info("✅ Patient found: {}", patientDto);
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Retrieves all patients (active and inactive).
     *
     * @return a list of all patients
     */
    @Operation(summary = "Get all patients (active and inactive)")
    @GetMapping("")
    public ResponseEntity<List<PatientDTO>> getPatients() {
        log.info("[GET] Retrieving all patients");
        List<Patient> patients = patientService.getAllPatients();
        List<PatientDTO> patientDtos = patientService.convertToDTOList(patients);
        log.info("✅ {} patient(s) retrieved", patientDtos.size());
        return ResponseEntity.ok(patientDtos);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PatientDTO>> getPatientsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[GET] Retrieving paginated list of all patients (page={}, size={})", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").and(Sort.by("firstName")));
        Page<PatientDTO> patientPage = patientService.getAllActivePatientsPage(pageable);
        log.info("✅ {} patient(s) found", patientPage.getTotalElements());
        return ResponseEntity.ok(patientPage);
    }

    /**
     * Retrieves all active patients.
     *
     * @return a list of active patients
      */
    @Operation(summary = "Get all active patients")
    @GetMapping("/active")
    public ResponseEntity<List<PatientDTO>> getActivePatients() {
        log.info("[GET] Retrieving active patients");
        List<Patient> patients = patientService.getAllActivePatients();
        List<PatientDTO> patientDtos = patientService.convertToDTOList(patients);
        log.info("✅ {} active patient(s) retrieved", patientDtos.size());
        return ResponseEntity.ok(patientDtos);
    }

    /**
     * Retrieves a paginated list of all patients (active and inactive).
     *
     * @param page Page number (default = 0)
     * @param size Number of records per page (default = 10)
     * @return Paginated list of patients
     */
    @Operation(summary = "Get paginated list of all patients")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    })
    @GetMapping("/active/page")
    public ResponseEntity<Page<PatientDTO>> getActivePatientsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[GET] Retrieving paginated active patients (page={}, size={})", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").and(Sort.by("firstName")));
        Page<PatientDTO> patientPage = patientService.getAllActivePatientsPaginated(pageable);
        log.info("✅ {} active patient(s) found", patientPage.getTotalElements());
        return ResponseEntity.ok(patientPage);
    }

    /**
     * Retrieves a patient by their last name.
     *
     * @param lastName the last name of the patient
     * @return the patient DTO
     */
    @Operation(summary = "Get a patient by last name")
    @GetMapping("/name/{lastName}")
    public ResponseEntity<PatientDTO> getPatientByLastName(
            @Parameter(description = "Patient last name") @PathVariable String lastName) {
        log.info("[GET] Retrieving patient by last name: {}", lastName);
        Patient patient = patientService.findPatientActiveByName(lastName);
        if (patient == null) {
            log.warn("❌ No patient found with last name: {}", lastName);
            return ResponseEntity.notFound().build();
        }
        PatientDTO patientDto = patientService.convertToDTO(patient);
        log.info("✅ Patient found: {}", patientDto);
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Retrieves a paginated list of active patients.
     *
     * @param page Page number
     * @param size Number of records per page
     * @return Paginated list of active patients
     */
    @Operation(summary = "Get paginated list of active patients")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active patients retrieved successfully")
    })
    @GetMapping("/active/search")
    public ResponseEntity<Page<PatientDTO>> getPatientByLastNamePage(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("[GET] Searching active patients by last name: '{}', page={}, size={}", name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").and(Sort.by("firstName")));
        Page<PatientDTO> patientPage = patientService.findPatientActiveByNamePaginated(name, pageable);
        log.info("✅ {} result(s) found", patientPage.getTotalElements());
        return ResponseEntity.ok(patientPage);
    }

    /**
     * Searches for active patients by last name (paginated).
     *
     * @param name Partial or full last name to search
     * @param page Page number
     * @param size Page size
     * @return Paginated list of matching patients
     */
    @Operation(summary = "Search active patients by last name (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching active patients found")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<PatientDTO>> searchPatientsPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[GET] Searching all patients by name='{}' (page={}, size={})", name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").and(Sort.by("firstName")));
        Page<PatientDTO> results = patientService.searchPatientsPaginated(name, pageable);
        log.info("✅ {} result(s) found", results.getTotalElements());
        return ResponseEntity.ok(results);
    }

    /**
     * Updates an existing patient by ID.
     *
     * @param id the patient UID
     * @param patientDTO the new data
     * @return the updated patient DTO
     */
    @Operation(summary = "Update an existing patient by ID")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable String id,
            @Valid @RequestBody PatientDTO patientDTO) {
        log.info("[PUT] Updating patient ID: {} with data: {}", id, patientDTO);
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        if (updatedPatient == null) {
            log.warn("❌ Patient not found for update: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("✅ Patient updated: {}", updatedPatient);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * Toggles the active status of a patient.
     * Only accessible to users with the ADMIN role.
     *
     * @param id the patient UID
     * @return the updated patient DTO
     */
    @Operation(summary = "Toggle a patient's active status (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle/{id}")
    public ResponseEntity<PatientDTO> togglePatient(
            @Parameter(description = "Patient ID") @PathVariable String id) {
        log.info("[PUT] Toggling active status for patient ID: {}", id);
        PatientDTO patientDto = patientService.toggleActivePatient(id);
        log.info("✅ Patient status updated: {}", patientDto);
        return ResponseEntity.ok().body(patientDto);
    }

    /**
     * Checks if a patient is active.
     *
     * @param uid the patient UID
     * @return {@code true} if active, otherwise {@code false}
     */
    @Operation(summary = "Check if a patient exists and is active")
    @GetMapping("/{uid}/exists")
    public boolean isActivePatient(
            @Parameter(description = "Patient UID") @PathVariable String uid) {
        log.info("[GET] Checking if patient UID exists or is active: {}", uid);
        boolean exists = patientService.isActive(uid);
        log.info("✅ Exists or active: {}", exists);
        return exists;
    }

    /**
     * Deletes a patient by UID.
     * Only accessible to users with the ADMIN role.
     *
     * @param id the patient UID
     * @return 204 No Content on success
     */
    @Operation(summary = "Delete a patient by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable String id) {
        log.info("[DELETE] Deleting patient ID: {}", id);
        patientService.deletePatient(id);
        log.info("✅ Patient deleted");
        return ResponseEntity.noContent().build();
    }
}
