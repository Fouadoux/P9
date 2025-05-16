package com.glucovision.noteservice.repository;

import com.glucovision.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Note} documents in MongoDB.
 * <p>
 * Provides methods to query notes by patient ID and creation date.
 */
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Retrieves all notes associated with a given patient ID.
     *
     * @param patientId the ID of the patient
     * @return a list of notes linked to the specified patient
     */
    List<Note> findAllByPatientId(String patientId);

    /**
     * Finds a specific note by patient ID and its exact creation date.
     *
     * @param patientId the ID of the patient
     * @param date the creation date of the note
     * @return an optional containing the note, if found
     */
    Optional<Note> findByPatientIdAndCreationDate(String patientId, LocalDateTime date);
}
