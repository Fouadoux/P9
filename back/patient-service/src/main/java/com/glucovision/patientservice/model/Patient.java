package com.glucovision.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table(name = "patients") // Table MySQL
public class Patient {
    public Patient() {
    }

    public Patient(Long id, String firstName, String lastName, String birthDate, Gender gender, String address, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = parseDate(birthDate);
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    public Patient(Long id, String firstName, String lastName, String birthDate, Gender gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = parseDate(birthDate);
        this.gender = gender;
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try{
                return LocalDate.parse(date, formatter);
            }catch (Exception ignored) {}
        }

        throw new RuntimeException("Could not parse date " + date);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;


}
