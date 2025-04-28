package com.glucovision.noteservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.ErrorResponse;
import com.glucovision.noteservice.exception.GlobalExceptionHandler;
import com.glucovision.noteservice.exception.PatientNotFoundException;
import com.glucovision.noteservice.service.NoteService;
import com.glucovision.noteservice.service.PatientDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NoteControllerTest {


    private MockMvc mockMvc;

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private NoteDto noteDto;

    @BeforeEach
    void setUp() {
        noteDto = new NoteDto();
        noteDto.setPatientId("patient123");
        noteDto.setComments("Test comment");
        noteDto.setCreationDate(LocalDateTime.now());
    }

    @Test
    void addNote_ShouldReturnCreatedResponse() {
        when(noteService.addNote(any(NoteDto.class))).thenReturn(noteDto);

        ResponseEntity<?> response = noteController.addNote(noteDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(noteDto, response.getBody());
        verify(noteService, times(1)).addNote(noteDto);
    }

    @Test
    void findById_ShouldReturnListOfNotes() {
        List<NoteDto> notes = Collections.singletonList(noteDto);
        when(noteService.findAllByPatientId("patient123")).thenReturn(notes);

        ResponseEntity<List<NoteDto>> response = noteController.findById("patient123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notes, response.getBody());
    }

    @Test
    void findById_ShouldReturnNotFoundWhenEmpty() {
        when(noteService.findAllByPatientId("patient123")).thenReturn(Collections.emptyList());

        ResponseEntity<List<NoteDto>> response = noteController.findById("patient123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateNote_ShouldReturnUpdatedNote() {
        when(noteService.updateNote(any(NoteDto.class))).thenReturn(noteDto);

        ResponseEntity<?> response = noteController.updateNote(noteDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(noteDto, response.getBody());
    }

    @Test
    void updateNote_ShouldHandleNoSuchElementException() {
        when(noteService.updateNote(any(NoteDto.class)))
                .thenThrow(new NoSuchElementException("Note non trouvée."));

        ResponseEntity<?> response = noteController.updateNote(noteDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Note non trouvée.", ((ErrorResponse) response.getBody()).getError());
    }

    @Test
    void updateNote_ShouldHandleIllegalStateException() {
        when(noteService.updateNote(any(NoteDto.class)))
                .thenThrow(new IllegalStateException("Modification expired"));

        ResponseEntity<?> response = noteController.updateNote(noteDto);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Modification expired", ((ErrorResponse) response.getBody()).getError());
    }

    @Test
    void deleteNote_ShouldReturnNoContent() {
        doNothing().when(noteService).deleteNote(any(NoteDto.class));

        ResponseEntity<?> response = noteController.deleteNote(noteDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteNote_ShouldHandleNoSuchElementException() {
        doThrow(new NoSuchElementException("Note non trouvée."))
                .when(noteService).deleteNote(any(NoteDto.class));

        ResponseEntity<?> response = noteController.deleteNote(noteDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Note non trouvée.", ((ErrorResponse) response.getBody()).getError());
    }

    @Test
    void deleteNote_ShouldHandleIllegalStateException() {
        doThrow(new IllegalStateException("Deletion expired"))
                .when(noteService).deleteNote(any(NoteDto.class));

        ResponseEntity<?> response = noteController.deleteNote(noteDto);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Deletion expired", ((ErrorResponse) response.getBody()).getError());
    }

    @Test
    void updateNoteAdmin_ShouldReturnUpdatedNote() {
        when(noteService.updateNoteForAdmin(any(NoteDto.class))).thenReturn(noteDto);

        ResponseEntity<?> response = noteController.updateNoteAdmin(noteDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(noteDto, response.getBody());
    }

    @Test
    void updateNoteAdmin_ShouldHandleNoSuchElementException() {
        when(noteService.updateNoteForAdmin(any(NoteDto.class)))
                .thenThrow(new NoSuchElementException("Note non trouvée."));

        ResponseEntity<?> response = noteController.updateNoteAdmin(noteDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Note non trouvée.", ((ErrorResponse) response.getBody()).getError());
    }

    @Test
    void deleteNoteAdmin_ShouldReturnNoContent() {
        doNothing().when(noteService).deleteNoteForAdmin(any(NoteDto.class));

        ResponseEntity<?> response = noteController.deleteNoteAdmin(noteDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteNoteAdmin_ShouldHandleNoSuchElementException() {
        doThrow(new NoSuchElementException("Note non trouvée."))
                .when(noteService).deleteNoteForAdmin(any(NoteDto.class));

        ResponseEntity<?> response = noteController.deleteNoteAdmin(noteDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Note non trouvée.", ((ErrorResponse) response.getBody()).getError());
    }
}