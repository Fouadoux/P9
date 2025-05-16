package com.glucovision.noteservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * Data Transfer Object representing a medical note associated with a patient.
 * <p>
 * This object is used for transferring note data between client and server.
 * It includes the patient ID, the note content, and timestamps for creation and last modification.
 */
@Data
@Schema(description = "Data Transfer Object representing a medical note linked to a patient")
public class NoteDto {

    /**
     * Default constructor.
     */
    public NoteDto() {
    }

    /**
     * Constructor with essential fields.
     *
     * @param patientId    the unique identifier of the patient
     * @param comments     the content of the note
     * @param creationDate the date and time the note was created
     */
    public NoteDto(String patientId, String comments, LocalDateTime creationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    /**
     * Unique identifier of the patient associated with this note.
     */
    @NotNull(message = "PatientId is required")
    @Schema(description = "Unique identifier of the patient linked to this note", example = "8e1f3b34-5c7e-4c17-9df6-bc6dd40efc62", required = true)
    private String patientId;

    /**
     * Content of the medical note.
     */
    @NotBlank(message = "Comment is required")
    @Schema(description = "Content of the medical note", example = "Patient reports fatigue and increased thirst.", required = true)
    private String comments;

    /**
     * Timestamp of when the note was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "Date and time when the note was created", example = "2025-05-13T14:22:31.000")
    private LocalDateTime creationDate;

    /**
     * Timestamp of when the note was last modified, if applicable.
     */
    @Schema(description = "Date and time when the note was last modified", example = "2025-05-14T09:10:00.000")
    private LocalDateTime modificationDate;
}
