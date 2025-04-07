package com.glucovision.patientservice.controller;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Ajouter un patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patient) {
        Patient savedPatient = patientService.addPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }


    // Récupérer un patient par ID
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.findPatientById(id);
        PatientDTO patientDto=patientService.convertToDTO(patient);
        return ResponseEntity.ok(patientDto);
    }

    @GetMapping("")
    public ResponseEntity<List<PatientDTO>> getPatients() {
        List<Patient> patients=patientService.findAll();
        List<PatientDTO> patientDtos=patientService.convertToDTOList(patients);
        return ResponseEntity.ok(patientDtos);
    }

    // Récupérer un patient par son nom
    @GetMapping("/name/{lastName}")
    public ResponseEntity<PatientDTO> getPatientByName(@PathVariable String lastName) {
        Patient patient = patientService.findPatientByName(lastName);
        PatientDTO patientDto=patientService.convertToDTO(patient);

        return patientDto != null ? ResponseEntity.ok(patientDto) : ResponseEntity.notFound().build();
    }

    // Mettre à jour un patient
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {

        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);

        return updatedPatient != null
                ? ResponseEntity.ok(updatedPatient)
                : ResponseEntity.notFound().build();
    }

    // Supprimer un patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }




    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFoundException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

}
