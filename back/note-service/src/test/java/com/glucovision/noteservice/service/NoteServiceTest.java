package com.glucovision.noteservice.service;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;


    @Test
    void convertToDto() {
        LocalDateTime creationDate = LocalDateTime.now();
        NoteDto noteDto = new NoteDto(1L,"test",creationDate);
        Note note  = new Note(noteDto.getPatientId(),noteDto.getComments(),noteDto.getCreationDate());

        NoteDto returnedNoteDto = noteService.convertToDto(note);
        assertEquals(noteDto.getComments(),returnedNoteDto.getComments());
        assertEquals(noteDto.getCreationDate(),returnedNoteDto.getCreationDate());
        assertEquals(noteDto.getPatientId(),returnedNoteDto.getPatientId());
    }

    @Test
    void convertToEntity() {
        LocalDateTime creationDate = LocalDateTime.now();
        NoteDto noteDto = new NoteDto(1L,"test",creationDate);
        Note savedNote  = new Note(noteDto.getPatientId(),noteDto.getComments(),noteDto.getCreationDate());

        Note returnedNote = noteService.convertToEntity(noteDto);
        assertEquals(savedNote.getId(),returnedNote.getId());
        assertEquals(savedNote.getComments(),returnedNote.getComments());
    }

    @Test
    void addNote() {
        LocalDateTime creationDate = LocalDateTime.now();
        NoteDto noteDto = new NoteDto(1L,"test",creationDate);
        Note savedNote  = new Note(noteDto.getPatientId(),noteDto.getComments(),noteDto.getCreationDate());

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        NoteDto result = noteService.addNote(noteDto);
        assertNotNull(result);
        assertEquals(noteDto.getComments(),result.getComments());
    }

    @Test
    void findAllByPatientId() {
        LocalDateTime creationDate = LocalDateTime.now();
        NoteDto noteDto1 = new NoteDto(1L,"test",creationDate);
        NoteDto noteDto2 = new NoteDto(1L,"test 2",creationDate);

        Note note1 = new Note(1L,"test",creationDate);
        Note note2 = new Note(1L,"test 2",creationDate);

        List<NoteDto> noteDtos = Arrays.asList(noteDto1,noteDto2);
        List<Note> notes = Arrays.asList(note1,note2);
        when(noteRepository.findAllByPatientId(1L)).thenReturn(notes);

        List<NoteDto> result = noteService.findAllByPatientId(1L);
        assertNotNull(result);
        assertEquals(noteDtos.size(),result.size());
        assertEquals(1L, result.get(0).getPatientId());
        assertEquals("test", result.get(0).getComments());
        verify(noteRepository).findAllByPatientId(1L);
    }

    @Test
    void testUpdateNote_Valid() {
        LocalDateTime creationDate = LocalDateTime.now();
        NoteDto noteDto = new NoteDto(1L,"test",creationDate);
        Note note = new Note(1L,"test updated",creationDate);

        when(noteRepository.findByPatientIdAndCreationDate(noteDto.getPatientId(),noteDto.getCreationDate()))
                .thenReturn(Optional.of(note));

        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto result = noteService.updateNote(noteDto.getPatientId(),noteDto.getCreationDate(),"test updated");

        assertNotNull(result);
        assertEquals(note.getComments(),result.getComments());
        assertNotNull(result.getModificationDate());

        verify(noteRepository).findByPatientIdAndCreationDate(noteDto.getPatientId(),noteDto.getCreationDate());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void testUpdateNote_fail_moreThanDay() {
        LocalDateTime oldCreationDate = LocalDateTime.now().minusHours(25);
        Note note = new Note(1L, "old comment", oldCreationDate);

        when(noteRepository.findByPatientIdAndCreationDate(1L, oldCreationDate))
                .thenReturn(Optional.of(note));

        // ACT + ASSERT
        assertThrows(IllegalStateException.class, () ->
                noteService.updateNote(1L, oldCreationDate, "new comment")
        );

        // Vérifie que save n’a pas été appelé
        verify(noteRepository, never()).save(any());
    }

    @Test
    void testUpdateNote_fail_notFound() {
        LocalDateTime oldCreationDate = LocalDateTime.now().minusHours(25);

        assertThrows(NoSuchElementException.class, () ->
                noteService.updateNote(1L, oldCreationDate, "new comment")
        );

        verify(noteRepository, never()).save(any());
    }

    @Test
    void deleteNote() {

        LocalDateTime creationDate = LocalDateTime.now();
        Note note=new Note(1L,"test",creationDate);
        when(noteRepository.findByPatientIdAndCreationDate(1L, creationDate)).thenReturn(Optional.of(note));

        noteService.deleteNote(1L,creationDate);
        verify(noteRepository).findByPatientIdAndCreationDate(1L, creationDate);
        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNote_fail_notFound() {
        LocalDateTime oldCreationDate = LocalDateTime.now().minusHours(25);
        assertThrows(NoSuchElementException.class, () ->
                noteService.deleteNote(1L, oldCreationDate)
        );

        verify(noteRepository).findByPatientIdAndCreationDate(1L, oldCreationDate);
        verify(noteRepository, never()).delete(any());
    }

}