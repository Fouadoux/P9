package com.glucovision.noteservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class NoteDto {

    public NoteDto() {
    }

    public NoteDto(Long patientId, String comments) {
        this.patientId = patientId;
        this.comments = comments;
    }

    public NoteDto(Long patientId, String comments, LocalDateTime creationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public NoteDto(Long patientId, String comments, LocalDateTime creationDate, LocalDateTime modificationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    @NotNull(message = "PatientId is required")
    private Long patientId;

    @NotBlank(message = "Comment is required")
    private String comments;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime  creationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime modificationDate;
}
