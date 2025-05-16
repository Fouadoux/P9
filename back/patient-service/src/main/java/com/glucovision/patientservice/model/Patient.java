package com.glucovision.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Entity representing a patient in the GlucoVision system.
 * <p>
 * Each patient has a unique UID and is defined by their personal and contact information.
 */
@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {

    /**
     * Default constructor for JPA.
     */
    public Patient() {}

    /**
     * Constructs a new active patient with the given attributes.
     *
     * @param firstName the first name of the patient
     * @param lastName the last name of the patient
     * @param birthDate the birth date as a String (accepted formats: yyyy-MM-dd, dd-MM-yyyy)
     * @param gender the patient's gender
     * @param address the patient's address
     * @param phone the patient's phone number
     */
    public Patient(String firstName, String lastName, String birthDate, Gender gender, String address, String phone) {
        this.uid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = parseDate(birthDate);
        this.gender = gender;
        this.address = address != null ? address : "";
        this.phone = phone != null ? phone : "";
        this.active = true;
    }

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private String uid;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "address")
    private String address = "";

    @Column(name = "phone")
    private String phone = "";

    @Column(nullable = false)
    private Boolean active = true;

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    /**
     * Tries to parse the given date string using accepted formats.
     *
     * @param date the date string
     * @return a parsed {@link LocalDate}
     * @throws RuntimeException if no formatter could parse the date
     */
    public LocalDate parseDate(String date) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (Exception ignored) {
                // continue trying other formats
            }
        }
        throw new RuntimeException("Could not parse date: " + date);
    }
}
