package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.PatientClient;
import com.glucovision.diabetesriskservice.dto.PatientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientDataServiceTest {

    @Mock
    private PatientClient patientClient;

    @InjectMocks
    private PatientDataService patientDataService;

    private final String VALID_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final PatientDto SAMPLE_PATIENT = new PatientDto(
            VALID_ID,
            LocalDate.of(1985, 5, 15),
            "M"
    );

    @Test
    void getPatient_shouldReturnPatient_whenValidId() {
        // Arrange
        when(patientClient.getPatientById(VALID_ID)).thenReturn(SAMPLE_PATIENT);

        // Act
        PatientDto result = patientDataService.getPatient(VALID_ID);

        // Assert
        assertNotNull(result);
        assertEquals(VALID_ID, result.getId());
        verify(patientClient).getPatientById(VALID_ID);
    }

    @Test
    void getPatient_shouldThrowIllegalArgumentException_whenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientDataService.getPatient(null));

        assertEquals("L'ID du patient ne peut pas être null ou vide", exception.getMessage());
        verifyNoInteractions(patientClient);
    }

    @Test
    void getPatient_shouldThrowIllegalArgumentException_whenIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientDataService.getPatient(""));

        assertEquals("L'ID du patient ne peut pas être null ou vide", exception.getMessage());
        verifyNoInteractions(patientClient);
    }

    @Test
    void getPatient_shouldThrowIllegalArgumentException_whenIdIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientDataService.getPatient("   "));

        assertEquals("L'ID du patient ne peut pas être null ou vide", exception.getMessage());
        verifyNoInteractions(patientClient);
    }

    @Test
    void getPatient_shouldPropagateClientExceptions() {
        // Arrange
        when(patientClient.getPatientById(VALID_ID))
                .thenThrow(new RuntimeException("Erreur de connexion"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> patientDataService.getPatient(VALID_ID));

        verify(patientClient).getPatientById(VALID_ID);
    }

    @Test
    void getPatient_shouldHandleSpecialCharactersInId() {
        // Arrange
        String specialId = "special-id-123!@#";
        when(patientClient.getPatientById(specialId)).thenReturn(SAMPLE_PATIENT);

        // Act
        PatientDto result = patientDataService.getPatient(specialId);

        // Assert
        assertNotNull(result);
        verify(patientClient).getPatientById(specialId);
    }

    @Test
    void getPatient_shouldReturnNull_whenClientReturnsNull() {
        // Arrange
        when(patientClient.getPatientById(VALID_ID)).thenReturn(null);

        // Act
        PatientDto result = patientDataService.getPatient(VALID_ID);

        // Assert
        assertNull(result);
        verify(patientClient).getPatientById(VALID_ID);
    }
}