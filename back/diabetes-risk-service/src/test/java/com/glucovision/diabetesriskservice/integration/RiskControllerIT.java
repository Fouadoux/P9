package com.glucovision.diabetesriskservice.integration;

import com.glucovision.diabetesriskservice.controller.RiskController;
import com.glucovision.diabetesriskservice.dto.RiskDto;
import com.glucovision.diabetesriskservice.integration.TestSecurityUtils;
import com.glucovision.diabetesriskservice.model.RiskLevel;
import com.glucovision.diabetesriskservice.service.RiskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(RiskController.class)
public class RiskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskService riskService;

    private final String riskLevel = "Medium";
    private final String patientId = "12345";
    private final String apiUrl = "/api/risk/patient/" + patientId;

    @Test
    void getRiskLevel_shouldReturnValidResponse() throws Exception {
        // Given
        String expectedRiskLevel = "BORDERLINE"; // Doit correspondre à ce que vous mockez
        given(riskService.evaluateRiskLevel(anyString()))
                .willReturn(RiskLevel.valueOf(expectedRiskLevel));

        // When & Then
        mockMvc.perform(get(apiUrl)
                        .with(TestSecurityUtils.mockWriter()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.riskLevel").value(expectedRiskLevel))
                .andReturn();
    }

    @Test
    void getRiskLevel_shouldCallServiceWithCorrectParameter() throws Exception {
        // Given
        given(riskService.evaluateRiskLevel(patientId))
                .willReturn(RiskLevel.BORDERLINE);

        // When
        mockMvc.perform(get(apiUrl)
                .with(TestSecurityUtils.mockWriter()));

        // Then
        verify(riskService).evaluateRiskLevel(patientId);
    }
}