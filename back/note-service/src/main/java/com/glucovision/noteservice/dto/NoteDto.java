package com.glucovision.noteservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class NoteDto {

    public NoteDto() {
    }

    public NoteDto(String patientId, String comments) {
        this.patientId = patientId;
        this.comments = comments;
    }

    public NoteDto(String patientId, String comments, LocalDateTime creationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public NoteDto(String patientId, String comments, LocalDateTime creationDate, LocalDateTime modificationDate) {
        this.patientId = patientId;
        this.comments = comments;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    @NotNull(message = "PatientId is required")
    private String patientId;

    @NotBlank(message = "Comment is required")
    private String comments;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime  creationDate;

    private LocalDateTime modificationDate;
}
