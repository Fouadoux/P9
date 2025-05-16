package com.glucovision.diabetesriskservice.dto;

import com.glucovision.diabetesriskservice.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the diabetes risk evaluation result for a patient.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Diabetes risk level result for a patient")
public class RiskDto {

    @Schema(description = "Patient ID for whom the risk was evaluated", example = "8e1f3b34-5c7e-4c17-9df6-bc6dd40efc62", required = true)
    private String patientId;

    @Schema(description = "Diabetes risk level assigned to the patient", example = "IN_DANGER", required = true)
    private RiskLevel riskLevel;
}
