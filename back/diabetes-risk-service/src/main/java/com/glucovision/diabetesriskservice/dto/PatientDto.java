package com.glucovision.diabetesriskservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Patient data used by the risk evaluation service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Basic patient information required for diabetes risk evaluation")
public class PatientDto {

    @Schema(description = "Unique identifier of the patient", example = "8e1f3b34-5c7e-4c17-9df6-bc6dd40efc62", required = true)
    private String id;

    @Schema(description = "Patient's birth date", example = "1985-04-23", required = true)
    private LocalDate birthDate;

    @Schema(description = "Patient's gender (e.g., MALE, FEMALE, OTHER)", example = "MALE", required = true)
    private String gender;
}
