package com.glucovision.diabetesriskservice.controller;

import com.glucovision.diabetesriskservice.dto.RiskDto;
import com.glucovision.diabetesriskservice.service.RiskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk") // ✅
@AllArgsConstructor
public class RiskController {

    private final RiskService service;

    //@PreAuthorize("hasRole('USER')")
    @GetMapping("/patient/{id}")
    public ResponseEntity<RiskDto> getRiskLevel(@PathVariable String id) {
        RiskDto riskDto = new RiskDto(id, service.evaluateRiskLevel(id));
        return ResponseEntity.ok(riskDto);
    }

}
