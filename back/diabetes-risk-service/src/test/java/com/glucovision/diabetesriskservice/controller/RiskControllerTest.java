package com.glucovision.diabetesriskservice.controller;

import com.glucovision.diabetesriskservice.model.RiskLevel;
import com.glucovision.diabetesriskservice.service.RiskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RiskService riskService;

    @TestConfiguration
    static class RiskServiceTestConfig {
        @Bean
        public RiskService riskService() {
            return Mockito.mock(RiskService.class);
        }
    }

    @Test
    void testEvaluateRisk_shouldReturnRiskLevel() throws Exception {
        // Given
        Long patientId = 1L;
        RiskLevel riskLevel = RiskLevel.BORDERLINE;

        when(riskService.evaluateRiskLevel(patientId)).thenReturn(riskLevel);

        // When + Then
        mockMvc.perform(get("/api/risk/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("BORDERLINE")); // ✅ on vérifie uniquement le champ
    }

}