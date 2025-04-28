package com.glucovision.patientservice.controller;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patient) {
        log.info("POST /api/patients - Création d'un nouveau patient : {}", patient);
        Patient savedPatient = patientService.addPatient(patient);
        log.info("Patient créé avec l'id : {}", savedPatient.getUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@Valid @PathVariable UUID id) {
        log.info("GET /api/patients/{} - Récupération du patient", id);
        Patient patient = patientService.findPatientById(id);
        PatientDTO patientDto = patientService.convertToDTO(patient);
        log.info("Patient récupéré : {}", patientDto);
        return ResponseEntity.ok(patientDto);
    }

    @GetMapping("")
    public ResponseEntity<List<PatientDTO>> getPatients() {
        log.info("GET /api/patients - Récupération de tous les patients");
        List<Patient> patients = patientService.getAllPatients();
        List<PatientDTO> patientDtos = patientService.convertToDTOList(patients);
        log.info("Nombre de patients récupérés : {}", patientDtos.size());
        return ResponseEntity.ok(patientDtos);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PatientDTO>> getPatientsActive() {
        log.info("GET /api/patients/active - Récupération des patients actifs");
        List<Patient> patients = patientService.getAllActivePatients();
        List<PatientDTO> patientDtos = patientService.convertToDTOList(patients);
        log.info("Nombre de patients actifs récupérés : {}", patientDtos.size());
        return ResponseEntity.ok(patientDtos);
    }

    @GetMapping("/name/{lastName}")
    public ResponseEntity<PatientDTO> getPatientByName(@PathVariable String lastName) {
        log.info("GET /api/patients/name/{} - Récupération du patient par nom", lastName);
        Patient patient = patientService.findPatientByName(lastName);

        if (patient == null) {
            log.warn("Patient avec le nom '{}' non trouvé", lastName);
            return ResponseEntity.notFound().build();
        }

        PatientDTO patientDto = patientService.convertToDTO(patient);
        log.info("Patient trouvé : {}", patientDto);
        return ResponseEntity.ok(patientDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientDTO patientDTO) {

        log.info("PUT /api/patients/{} - Mise à jour du patient : {}", id, patientDTO);
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);

        if (updatedPatient == null) {
            log.warn("Patient avec l'id '{}' non trouvé pour mise à jour", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Patient mis à jour : {}", updatedPatient);
        return ResponseEntity.ok(updatedPatient);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle/{id}")
    public ResponseEntity<PatientDTO> togglePatient(@PathVariable UUID id) {
        log.info("PUT /api/patients/toggle/{} - Activation/Désactivation du patient", id);
        PatientDTO patientDto = patientService.toggleActivePatient(id);
        log.info("Nouveau statut du patient : {}", patientDto);
        return ResponseEntity.ok().body(patientDto);
    }

    @GetMapping("/{uid}/exists")
    public boolean isActivePatient(@PathVariable String uid) {
        return patientService.ifActiveOrExistingPatient(UUID.fromString(uid));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
