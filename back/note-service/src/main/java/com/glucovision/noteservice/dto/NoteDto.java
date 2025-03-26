package com.glucovision.noteservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class NoteDto {

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String comments;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime  creationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime modificationDate;
}
