package com.glucovision.patientservice.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.glucovision.patientservice.model.Patient;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByLastName(String lastName);

    Optional<Patient> findByUid(String uid);
    List<Patient> findByActiveTrue(Sort sort);
    List<Patient> findByActiveTrue();

    boolean existsByUid(String uid);

    void deleteByUid(String uid);
}
