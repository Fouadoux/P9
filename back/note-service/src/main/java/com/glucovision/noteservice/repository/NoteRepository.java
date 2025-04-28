package com.glucovision.noteservice.repository;

import com.glucovision.noteservice.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByPatientId(String patientId);
    Optional<Note> findByPatientIdAndCreationDate(String patientId, LocalDateTime date);
}
