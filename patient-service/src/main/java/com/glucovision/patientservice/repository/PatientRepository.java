package com.glucovision.patientservice.repository;

import com.glucovision.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByLastName(String lastName);
}
