package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.NoteClient;
import com.glucovision.diabetesriskservice.dto.NoteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteDataServiceTest {

    @Mock
    private NoteClient noteClient;

    @InjectMocks
    private NoteDataService noteDataService;

    private final String PATIENT_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final NoteDto NOTE_1 = new NoteDto("1", "Première note");
    private final NoteDto NOTE_2 = new NoteDto("2", "Deuxième note");

    @Test
    void getNoteDtoList_shouldReturnListOfNotes_whenClientReturnsNotes() {
        // Arrange
        List<NoteDto> expectedNotes = Arrays.asList(NOTE_1, NOTE_2);
        when(noteClient.getNoteListe(PATIENT_ID)).thenReturn(expectedNotes);

        // Act
        List<NoteDto> result = noteDataService.getNoteDtoList(PATIENT_ID);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedNotes, result);
        verify(noteClient, times(1)).getNoteListe(PATIENT_ID);
    }

    @Test
    void getNoteDtoList_shouldReturnEmptyList_whenClientReturnsEmptyList() {
        // Arrange
        when(noteClient.getNoteListe(PATIENT_ID)).thenReturn(Collections.emptyList());

        // Act
        List<NoteDto> result = noteDataService.getNoteDtoList(PATIENT_ID);

        // Assert
        assertTrue(result.isEmpty());
        verify(noteClient, times(1)).getNoteListe(PATIENT_ID);
    }

    @Test
    void getNoteDtoList_shouldThrowException_whenClientThrowsException() {
        // Arrange
        when(noteClient.getNoteListe(PATIENT_ID)).thenThrow(new RuntimeException("Erreur de connexion"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            noteDataService.getNoteDtoList(PATIENT_ID);
        });
        verify(noteClient, times(1)).getNoteListe(PATIENT_ID);
    }

    @Test
    void getNoteDtoList_shouldThrowIllegalArgumentException_whenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            noteDataService.getNoteDtoList(null);
        });
        verifyNoInteractions(noteClient);
    }

    @Test
    void getNoteDtoList_shouldHandleSpecialCharactersInId() {
        // Arrange
        String specialId = "special-id-123!@#";
        when(noteClient.getNoteListe(specialId)).thenReturn(Collections.singletonList(NOTE_1));

        // Act
        List<NoteDto> result = noteDataService.getNoteDtoList(specialId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(NOTE_1, result.get(0));
        verify(noteClient, times(1)).getNoteListe(specialId);
    }

    @Test
    void getNoteDtoList_shouldHandleVeryLongPatientIds() {
        String longId = "a".repeat(1000);
        when(noteClient.getNoteListe(longId)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> noteDataService.getNoteDtoList(longId));
    }

    @Test
    void getNoteDtoList_shouldPreserveNoteOrder() {
        List<NoteDto> notes = Arrays.asList(NOTE_1, NOTE_2);
        when(noteClient.getNoteListe(PATIENT_ID)).thenReturn(notes);

        List<NoteDto> result = noteDataService.getNoteDtoList(PATIENT_ID);

        assertEquals(NOTE_1, result.get(0)); // Vérifie l'ordre
        assertEquals(NOTE_2, result.get(1));
    }



    @Test
    void getNoteDtoList_shouldThrowIllegalArgumentException_whenIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteDataService.getNoteDtoList("");
        });

        assertEquals("L'ID du patient ne peut pas être null ou vide", exception.getMessage());
        verifyNoInteractions(noteClient);
    }

    @Test
    void getNoteDtoList_shouldThrowIllegalArgumentException_whenIdIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteDataService.getNoteDtoList("   ");
        });

        assertEquals("L'ID du patient ne peut pas être null ou vide", exception.getMessage());
        verifyNoInteractions(noteClient);
    }
}