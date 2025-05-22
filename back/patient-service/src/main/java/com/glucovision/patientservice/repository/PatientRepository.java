package com.glucovision.patientservice.repository;

import com.glucovision.patientservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Patient} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations, and declares
 * additional query methods for custom access patterns.
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their last name.
     *
     * @param lastName the last name of the patient
     * @return an {@link Optional} containing the patient if found, or empty otherwise
     */
    Optional<Patient> findByLastName(String lastName);

    /**
     * Finds a patient by their unique UID (UUID-based identifier).
     *
     * @param uid the UID of the patient
     * @return an {@link Optional} containing the patient if found, or empty otherwise
     */
    Optional<Patient> findByUid(String uid);

    /**
     * Retrieves all active patients, sorted according to the provided {@link Sort} parameter.
     *
     * @param sort the sort criteria
     * @return a list of active patients
     */
    List<Patient> findByActiveTrue(Sort sort);

    /**
     * Checks whether a patient exists with the given UID.
     *
     * @param uid the UID to check
     * @return {@code true} if a patient exists with the given UID, {@code false} otherwise
     */
    Boolean existsByUid(String uid);

    /**
     * Deletes a patient by their UID.
     *
     * @param uid the UID of the patient to delete
     */
    void deleteByUid(String uid);

    boolean existsByFirstNameAndLastNameAndBirthDate(String firstName, String lastName, LocalDate birthDate);

    Page<Patient> findByActiveTrue(Pageable pageable);
    Page<Patient> findByActiveTrueAndLastNameContainingIgnoreCase(String lastName, Pageable pageable);
    Page<Patient> findByLastNameContainingIgnoreCase(String lastName,Pageable pageable);
}
