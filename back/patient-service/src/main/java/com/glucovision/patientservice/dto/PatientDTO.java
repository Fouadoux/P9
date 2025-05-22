package com.glucovision.patientservice.dto;

import com.glucovision.patientservice.model.Gender;
import com.glucovision.patientservice.model.PatientStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for transferring patient information between layers.
 * <p>
 * Represents both input (create/update) and output (read) data for patients.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object representing a patient")
public class PatientDTO {

    public PatientDTO(String uid, String firstName, String lastName, LocalDate birthDate, Gender gender, String address, String phone) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
    }

    @Schema(description = "Unique identifier (UUID) of the patient", example = "8e1f3b34-5c7e-4c17-9df6-bc6dd40efc62")
    private String uid;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(description = "First name of the patient", example = "John", required = true)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(description = "Last name of the patient", example = "Doe", required = true)
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Schema(description = "Birth date of the patient", example = "1985-04-23", required = true)
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @Schema(description = "Gender of the patient (MALE, FEMALE, OTHER)", example = "MALE", required = true)
    private Gender gender;

    @Schema(description = "Address of the patient", example = "123 Main Street, Cityville")
    private String address;

    @Schema(description = "Phone number of the patient", example = "+33 6 12 34 56 78")
    @Pattern(regexp = "^$|\\d{10}", message = "Le numéro de téléphone doit contenir exactement 10 chiffres ou être vide.")
    private String phone;

    @Schema(description = "Whether the patient is active", example = "true", defaultValue = "true")
    private Boolean active = true;

    public PatientStatus getStatus() {
        return active ? PatientStatus.ACTIF : PatientStatus.INACTIF;
    }
}
