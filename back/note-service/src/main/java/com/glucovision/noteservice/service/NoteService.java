package com.glucovision.noteservice.service;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

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

    public NoteDto addNote(NoteDto noteDto){
        Note note = convertToEntity(noteDto);
        Note saveNote= noteRepository.save(note);
        return convertToDto(saveNote);
    }

    public List<NoteDto> findAllByPatientId(Long patientId) {
        return noteRepository.findAllByPatientId(patientId).stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    public NoteDto updateNote(Long patientId, LocalDateTime creationDate, String comments) {
        Note note = noteRepository.findByPatientIdAndCreationDate(patientId, creationDate)
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        LocalDateTime now = LocalDateTime.now();

        if (note.getCreationDate().plusHours(24).isBefore(now)) {
            throw new IllegalStateException("Modification interdite : plus de 24h se sont écoulées depuis la création de la note.");
        }        note.setModificationDate(LocalDateTime.now());

        note.setComments(comments);

        return convertToDto(noteRepository.save(note));
    }

    public void deleteNote(long patientId, LocalDateTime creationDate) {
        Note note = noteRepository.findByPatientIdAndCreationDate(patientId, creationDate)
                .orElseThrow(() -> new NoSuchElementException("Note introuvable"));

        if (note.getCreationDate().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Suppression interdite : plus de 24h se sont écoulées.");
        }

        noteRepository.delete(note);
    }


}
