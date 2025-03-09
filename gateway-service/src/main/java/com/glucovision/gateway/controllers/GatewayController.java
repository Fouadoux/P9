package com.glucovision.gateway.controllers;


import com.glucovision.gateway.clients.HistoryServiceClient;
import com.glucovision.gateway.clients.PatientServiceClient;
import com.glucovision.gateway.dto.CommentDTO;
import com.glucovision.gateway.dto.HistoryDTO;
import com.glucovision.gateway.dto.PatientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway/patients")
public class GatewayController {

    private final PatientServiceClient patientServiceClient;
    private final HistoryServiceClient historyServiceClient;

    public GatewayController(PatientServiceClient patientServiceClient, HistoryServiceClient historyServiceClient) {
        this.patientServiceClient = patientServiceClient;
        this.historyServiceClient = historyServiceClient;
    }

    //Patient

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientServiceClient.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patient = patientServiceClient.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        PatientDTO createdPatient = patientServiceClient.createPatient(patientDTO);
        return ResponseEntity.status(201).body(createdPatient);
    }

    //History

    @GetMapping("/history/{patientId}")
    public ResponseEntity<HistoryDTO> getPatientHistory(@PathVariable String patientId) {
        return ResponseEntity.ok(historyServiceClient.getHistoryByPatientId(patientId));
    }

    @PostMapping("/history/{patientId}/add-comment")
    public ResponseEntity<HistoryDTO> addComment(@PathVariable String patientId, @RequestBody CommentDTO comment) {
        return ResponseEntity.status(201).body(historyServiceClient.addCommentToHistory(patientId, comment));
    }


}
