package com.glucovision.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {


    public PatientDTO(Long id, String firstName, String lastName, LocalDate birthDate, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)  // Empêche la modification de l'ID
    private Long id;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastName;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate birthDate;

    @NotBlank(message = "Le genre est obligatoire")
    private String gender;

    private String address;
    private String phoneNumber;
}
