package com.glucovision.noteservice.service;

import com.glucovision.noteservice.client.PatientClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import feign.FeignException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientDataServiceTest {

    @Mock
    private PatientClient patientClient; // Mock du Feign Client

    @InjectMocks
    private PatientDataService patientDataService; // Service à tester

    @Test
    void isActivePatient_ShouldReturnTrue_WhenPatientIsActive() {
        // Arrange
        String patientId = "123e4567-e89b-12d3-a456-426614174000";
        when(patientClient.isActivePatient(patientId)).thenReturn(true);

        // Act
        boolean result = patientDataService.isActivePatient(patientId);

        // Assert
        assertTrue(result);
        verify(patientClient, times(1)).isActivePatient(patientId);
    }

    @Test
    void isActivePatient_ShouldReturnFalse_WhenPatientIsInactive() {
        // Arrange
        String patientId = "123e4567-e89b-12d3-a456-426614174000";
        when(patientClient.isActivePatient(patientId)).thenReturn(false);

        // Act
        boolean result = patientDataService.isActivePatient(patientId);

        // Assert
        assertFalse(result);
        verify(patientClient, times(1)).isActivePatient(patientId);
    }

    @Test
    void isActivePatient_ShouldThrowException_WhenClientFails() {
        // Arrange
        String patientId = "invalid-id";
        when(patientClient.isActivePatient(patientId))
                .thenThrow(FeignException.class); // Simule une erreur Feign

        // Act & Assert
        assertThrows(FeignException.class, () -> {
            patientDataService.isActivePatient(patientId);
        });
        verify(patientClient, times(1)).isActivePatient(patientId);
    }
}