package com.glucovision.noteservice.service;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.PatientNotFoundException;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Log4j2
@Service
@Data
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final PatientDataService patientDataService;

    public NoteDto convertToDto (Note note){
        NoteDto noteDto = new NoteDto();
        noteDto.setPatientId(note.getPatientId());
        noteDto.setComments(note.getComments());
        noteDto.setCreationDate(note.getCreationDate());
        noteDto.setModificationDate(note.getModificationDate());

        return noteDto;
    }

    public Note convertToEntity (NoteDto noteDto){
        Note note = new Note();
        note.setPatientId(noteDto.getPatientId());
        note.setComments(noteDto.getComments());
        if (noteDto.getCreationDate() != null) {
            note.setCreationDate(noteDto.getCreationDate());
        } else {
            note.setCreationDate(LocalDateTime.now());
        }
        return note;
    }

    public NoteDto addNote(NoteDto noteDto) {
        if (!patientDataService.isActivePatient(noteDto.getPatientId())) {
            log.info("Patient is not active.");
            throw new PatientNotFoundException("Patient not found");
        }
        log.info("Adding note to patient with id " + noteDto.getPatientId());
        Note note = convertToEntity(noteDto);
        Note saveNote = noteRepository.save(note);
        return convertToDto(saveNote);
    }

    public List<NoteDto> findAllByPatientId(String patientId) {
        return noteRepository.findAllByPatientId(patientId).stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public NoteDto updateNote(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        LocalDateTime now = LocalDateTime.now();

        if (note.getCreationDate().plusHours(24).isBefore(now)) {
            throw new IllegalStateException("Modification interdite : plus de 24h se sont écoulées depuis la création de la note.");
        }        note.setModificationDate(LocalDateTime.now());

        note.setComments(noteDto.getComments());

        return convertToDto(noteRepository.save(note));
    }

    public void deleteNote(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        if (note.getCreationDate().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Suppression interdite : plus de 24h se sont écoulées.");
        }

        noteRepository.delete(note);
    }


    public NoteDto updateNoteForAdmin(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));


        note.setModificationDate(LocalDateTime.now());
        note.setComments(noteDto.getComments());
        return convertToDto(noteRepository.save(note));
    }

    public void deleteNoteForAdmin(NoteDto noteDto) {
        Note note = noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate())
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        noteRepository.delete(note);
    }

}
