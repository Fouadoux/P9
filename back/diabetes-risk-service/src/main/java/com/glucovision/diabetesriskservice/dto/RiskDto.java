package com.glucovision.diabetesriskservice.dto;

import com.glucovision.diabetesriskservice.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskDto {
    private Long patientId;
    private RiskLevel riskLevel;
}
