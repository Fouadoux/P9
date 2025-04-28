package com.glucovision.noteservice.service;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.PatientNotFoundException;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private PatientDataService patientDataService;

    @InjectMocks
    private NoteService noteService;

    private NoteDto noteDto;
    private Note note;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        noteDto = new NoteDto();
        noteDto.setPatientId("patient123");
        noteDto.setComments("Test comment");
        noteDto.setCreationDate(now);

        note = new Note();
        note.setPatientId("patient123");
        note.setComments("Test comment");
        note.setCreationDate(now);
    }

    @Test
    void convertToDto_ShouldReturnCorrectDto() {
        NoteDto result = noteService.convertToDto(note);

        assertEquals(note.getPatientId(), result.getPatientId());
        assertEquals(note.getComments(), result.getComments());
        assertEquals(note.getCreationDate(), result.getCreationDate());
        assertEquals(note.getModificationDate(), result.getModificationDate());
    }

    @Test
    void convertToEntity_ShouldReturnCorrectEntity() {
        Note result = noteService.convertToEntity(noteDto);

        assertEquals(noteDto.getPatientId(), result.getPatientId());
        assertEquals(noteDto.getComments(), result.getComments());
        assertEquals(noteDto.getCreationDate(), result.getCreationDate());
    }

    @Test
    void convertToEntity_ShouldSetCurrentDateWhenCreationDateIsNull() {
        noteDto.setCreationDate(null);
        Note result = noteService.convertToEntity(noteDto);

        assertNotNull(result.getCreationDate());
        assertTrue(result.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void addNote_ShouldSaveNoteWhenPatientIsActive() {
        when(patientDataService.isActivePatient(noteDto.getPatientId())).thenReturn(true);
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto result = noteService.addNote(noteDto);

        assertNotNull(result);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void addNote_ShouldThrowExceptionWhenPatientIsNotActive() {
        when(patientDataService.isActivePatient(noteDto.getPatientId())).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> noteService.addNote(noteDto));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void findAllByPatientId_ShouldReturnListOfNotes() {
        when(noteRepository.findAllByPatientId("patient123")).thenReturn(Collections.singletonList(note));

        List<NoteDto> result = noteService.findAllByPatientId("patient123");

        assertEquals(1, result.size());
        assertEquals(note.getPatientId(), result.get(0).getPatientId());
    }

    @Test
    void findAllByPatientId_ShouldReturnEmptyListWhenNoNotes() {
        when(noteRepository.findAllByPatientId("patient123")).thenReturn(Collections.emptyList());

        List<NoteDto> result = noteService.findAllByPatientId("patient123");

        assertTrue(result.isEmpty());
    }

    @Test
    void updateNote_ShouldUpdateNoteWhenWithin24Hours() {
        LocalDateTime now = LocalDateTime.now();
        note.setCreationDate(now.minusHours(23));
        noteDto.setCreationDate(now.minusHours(23));

        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto result = noteService.updateNote(noteDto);

        assertNotNull(result.getModificationDate());
        assertEquals(noteDto.getComments(), result.getComments());
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void updateNote_ShouldThrowExceptionWhenNoteNotFound() {
        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> noteService.updateNote(noteDto));
    }

    @Test
    void updateNote_ShouldThrowExceptionWhenMoreThan24Hours() {
        note.setCreationDate(LocalDateTime.now().minusHours(25));
        noteDto.setCreationDate(LocalDateTime.now().minusHours(25));

        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));

        assertThrows(IllegalStateException.class, () -> noteService.updateNote(noteDto));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void deleteNote_ShouldDeleteNoteWhenWithin24Hours() {
        LocalDateTime now = LocalDateTime.now();
        note.setCreationDate(now.minusHours(23));
        noteDto.setCreationDate(now.minusHours(23));

        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));

        noteService.deleteNote(noteDto);

        verify(noteRepository, times(1)).delete(note);
    }

    @Test
    void deleteNote_ShouldThrowExceptionWhenMoreThan24Hours() {
        note.setCreationDate(LocalDateTime.now().minusHours(25));
        noteDto.setCreationDate(LocalDateTime.now().minusHours(25));

        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));

        assertThrows(IllegalStateException.class, () -> noteService.deleteNote(noteDto));
        verify(noteRepository, never()).delete(any(Note.class));
    }

    @Test
    void updateNoteForAdmin_ShouldUpdateNoteWithoutTimeRestriction() {
        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto result = noteService.updateNoteForAdmin(noteDto);

        assertNotNull(result.getModificationDate());
        assertEquals(noteDto.getComments(), result.getComments());
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void deleteNoteForAdmin_ShouldDeleteNoteWithoutTimeRestriction() {
        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(), noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));

        noteService.deleteNoteForAdmin(noteDto);

        verify(noteRepository, times(1)).delete(note);
    }
}