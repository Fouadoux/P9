package com.glucovision.diabetesriskservice.controller;

import com.glucovision.diabetesriskservice.dto.RiskDto;
import com.glucovision.diabetesriskservice.model.RiskLevel;
import com.glucovision.diabetesriskservice.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskControllerTest {

    @Mock
    private RiskService riskService;

    @InjectMocks
    private RiskController riskController;

    private final String patientId = "12345";

    @Test
    void getRiskLevel_shouldReturnRiskDtoWithCorrectValues() {
        // Arrange
        when(riskService.evaluateRiskLevel(patientId)).thenReturn(RiskLevel.BORDERLINE);

        // Act
        ResponseEntity<RiskDto> response = riskController.getRiskLevel(patientId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        RiskDto riskDto = response.getBody();
        assertNotNull(riskDto);
        assertEquals(patientId, riskDto.getPatientId());
        assertEquals(RiskLevel.BORDERLINE, riskDto.getRiskLevel());

        verify(riskService, times(1)).evaluateRiskLevel(patientId);
    }

    @Test
    void getRiskLevel_shouldCallServiceOnce() {
        // Arrange
        when(riskService.evaluateRiskLevel(patientId)).thenReturn(RiskLevel.BORDERLINE);

        // Act
        riskController.getRiskLevel(patientId);

        // Assert
        verify(riskService, times(1)).evaluateRiskLevel(patientId);
    }
}