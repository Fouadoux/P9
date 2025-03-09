package com.glucovision.patientservice.repository;

import com.glucovision.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByLastName(String lastName);
}
