package com.glucovision.noteservice.service;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.PatientNotFoundException;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
/**
 * Service class that handles business logic related to medical notes.
 * <p>
 * This includes operations such as adding, updating, deleting notes,
 * and converting between entity and DTO representations.
 * <p>
 * It also enforces validation rules, such as checking if a patient is active
 * and whether a note is eligible for modification or deletion.
 */
@Slf4j
@Service
@Data
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final PatientDataService patientDataService;

    /**
     * Converts a {@link Note} entity into a {@link NoteDto}.
     *
     * @param note the note entity
     * @return the corresponding DTO
     */
    public NoteDto convertToDto(Note note) {
        NoteDto noteDto = new NoteDto();
        noteDto.setPatientId(note.getPatientId());
        noteDto.setComments(note.getComments());
        noteDto.setCreationDate(note.getCreationDate());
        noteDto.setModificationDate(note.getModificationDate());
        return noteDto;
    }

    /**
     * Converts a {@link NoteDto} into a {@link Note} entity.
     * Sets the current time as creation date if none is provided.
     *
     * @param noteDto the DTO to convert
     * @return the entity version of the note
     */
    public Note convertToEntity(NoteDto noteDto) {
        Note note = new Note();
        note.setPatientId(noteDto.getPatientId());
        note.setComments(noteDto.getComments());
        note.setCreationDate(noteDto.getCreationDate() != null ? noteDto.getCreationDate() : LocalDateTime.now());
        return note;
    }

    /**
     * Adds a new note for a given patient if the patient is active.
     *
     * @param noteDto the note to add
     * @return the created note as DTO
     * @throws PatientNotFoundException if the patient is inactive or does not exist
     */
    public NoteDto addNote(NoteDto noteDto) {
        if (!patientDataService.isActivePatient(noteDto.getPatientId())) {
            log.info("Patient is not active.");
            throw new PatientNotFoundException("Patient not found");
        }
        log.info("Adding note to patient with id {}", noteDto.getPatientId());
        Note note = convertToEntity(noteDto);
        Note saveNote = noteRepository.save(note);
        return convertToDto(saveNote);
    }

    /**
     * Retrieves all notes for a given patient.
     *
     * @param patientId the ID of the patient
     * @return a list of note DTOs
     */
    public List<NoteDto> findAllByPatientId(String patientId) {
        return noteRepository.findAllByPatientId(patientId).stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Updates a note if it was created less than 24 hours ago.
     *
     * @param noteDto the updated note data
     * @return the updated note DTO
     * @throws NoSuchElementException if the note does not exist
     * @throws IllegalStateException  if the update window has expired
     */
    public NoteDto updateNote(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        if (note.getCreationDate().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Modification interdite : plus de 24h se sont écoulées depuis la création de la note.");
        }

        note.setModificationDate(LocalDateTime.now());
        note.setComments(noteDto.getComments());

        return convertToDto(noteRepository.save(note));
    }

    /**
     * Deletes a note if it was created less than 24 hours ago.
     *
     * @param noteDto the note to delete
     * @throws NoSuchElementException if the note does not exist
     * @throws IllegalStateException  if the deletion window has expired
     */
    public void deleteNote(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        if (note.getCreationDate().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Suppression interdite : plus de 24h se sont écoulées.");
        }

        noteRepository.delete(note);
    }

    /**
     * Admin-only operation to update a note without time restriction.
     *
     * @param noteDto the note to update
     * @return the updated note DTO
     * @throws NoSuchElementException if the note does not exist
     */
    public NoteDto updateNoteForAdmin(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        note.setModificationDate(LocalDateTime.now());
        note.setComments(noteDto.getComments());
        return convertToDto(noteRepository.save(note));
    }

    /**
     * Admin-only operation to delete a note without time restriction.
     *
     * @param noteDto the note to delete
     * @throws NoSuchElementException if the note does not exist
     */
    public void deleteNoteForAdmin(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        noteRepository.delete(note);
    }
}
