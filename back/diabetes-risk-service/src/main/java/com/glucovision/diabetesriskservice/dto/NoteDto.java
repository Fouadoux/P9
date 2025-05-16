package com.glucovision.diabetesriskservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight representation of a medical note used by the risk evaluation service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Note summary used in diabetes risk evaluation")
public class NoteDto {

    @Schema(description = "Patient ID associated with the note", example = "8e1f3b34-5c7e-4c17-9df6-bc6dd40efc62", required = true)
    private String patientId;

    @Schema(description = "Content of the medical note", example = "Patient experiences frequent urination and blurred vision.", required = true)
    private String comments;
}
