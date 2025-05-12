package com.glucovision.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {

    public Patient() {}

    public Patient(String firstName, String lastName, String birthDate, Gender gender, String address, String phone) {
        this.uid =  UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = parseDate(birthDate);
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.active = true;
    }

    public Patient(String uid,String firstName, String lastName, String birthDate, Gender gender, String address, String phone, boolean active) {
        this.uid = (uid != null && !uid.isBlank()) ? uid : UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = parseDate(birthDate);
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.active = active;
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
    private String address="";

    @Column(name = "phone")
    private String phone="";

    @Column(nullable = false)
    private Boolean active = true;

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    public LocalDate parseDate(String date) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Could not parse date: " + date);
    }
}
