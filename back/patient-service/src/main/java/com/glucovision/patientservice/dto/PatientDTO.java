package com.glucovision.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glucovision.patientservice.model.Gender;
import com.glucovision.patientservice.model.PatientStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {


    public PatientDTO(UUID uid, String firstName, String lastName, LocalDate birthDate, Gender gender) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }
    public PatientDTO(UUID uid, String firstName, String lastName, LocalDate birthDate, Gender gender,String address,String phone) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone=phone;
    }


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)  // Empêche la modification de l'ID
    private UUID uid;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastName;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String address;
    private String phone;
    private boolean active;

    public PatientStatus getStatus() {

        return active ? PatientStatus.ACTIF : PatientStatus.INACTIF;
    }
}
