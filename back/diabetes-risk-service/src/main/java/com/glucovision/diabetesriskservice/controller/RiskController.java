package com.glucovision.diabetesriskservice.controller;

import com.glucovision.diabetesriskservice.dto.RiskDto;
import com.glucovision.diabetesriskservice.service.RiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for evaluating the diabetes risk level of a patient.
 * <p>
 * This controller exposes an endpoint that takes a patient ID and returns the associated
 * diabetes risk level based on their age, gender, and medical notes.
 */
@RestController
@RequestMapping("/api/risk")
@AllArgsConstructor
@Slf4j
@Tag(name = "Diabetes Risk", description = "Endpoint for evaluating diabetes risk level for a patient")
public class RiskController {

    private final RiskService service;

    /**
     * Evaluates the diabetes risk level of a patient using their ID.
     *
     * @param id The unique identifier of the patient
     * @return A {@link ResponseEntity} containing the {@link RiskDto} with the risk level
     */
    @Operation(summary = "Evaluate the diabetes risk level of a patient by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risk level successfully evaluated"),
            @ApiResponse(responseCode = "404", description = "Patient not found or insufficient data")
    })
    @GetMapping("/patient/{id}")
    public ResponseEntity<RiskDto> getRiskLevel(
            @Parameter(description = "Patient ID") @PathVariable String id) {

        log.info("[GET] Request received to evaluate diabetes risk for patientId={}", id);

        RiskDto riskDto = new RiskDto(id, service.evaluateRiskLevel(id));

        log.info("✅ Risk evaluation complete: patientId={}, riskLevel={}", id, riskDto.getRiskLevel());

        return ResponseEntity.ok(riskDto);
    }
}
