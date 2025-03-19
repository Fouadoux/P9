package com.glucovision.patientservice.service;

import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")  // Charge application-test.properties
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    public void testFindAll() {
        List<Patient> patients= List.of(
                new Patient(1L,"John","Doe","1980-12-03","M","place monge","111-222-333"),
                new Patient(2L,"Fred","Jack","1989-04-03","M","rue pasteur","222-111-333"),
                new Patient(3L,"Fred","Jack","1989-04-03","M","rue france","333-222-111")
                );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result= patientService.findAll();
        assertNotNull(result);
        assertEquals(patients.size(), result.size());
    }

    @Test
    public void testFindByLastName_Found() {
        Patient patient= new Patient(1L,"John","Doe","1980-12-03","M","place monge","111-222-333");
        when(patientRepository.findByLastName("Doe")).thenReturn(Optional.of(patient));

        Patient result = patientService.findPatientByName("Doe");
        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
        assertEquals(patient.getLastName(), result.getLastName());
    }

    @Test
    public void testFindByLastName_NotFound() {
        when(patientRepository.findByLastName(null)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, ()->patientService.findPatientByName("Doe"));
    }

    @Test
    public void testAddPatient() {
        Patient patient= new Patient(1L,"John","Doe","1980-12-03","M","place monge","111-222-333");
        when(patientRepository.save(patient)).thenReturn(patient);
        Patient result = patientService.addPatient(patient);
        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
        assertEquals(patient.getLastName(), result.getLastName());

    }

    @Test
    void testAddPatient_WithNullPatient_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.addPatient(null);
        });

        assertEquals("Invalid patient data", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class)); // Vérifie que save() n'est jamais appelé
    }

    @Test
    void testAddPatient_WithInvalidFields_ShouldThrowException() {
        // Arrange
        Patient invalidPatient = new Patient();
        invalidPatient.setLastName("Doe");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.addPatient(invalidPatient);
        });

        assertEquals("Invalid patient data", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testFindPatientById_Found() {
        // Arrange
        Patient patient = new Patient(1L, "Alice", "Doe", ("1992-10-20"), "F");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        Patient result = patientService.findPatientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void testFindPatientById_NotFound() {
        // Arrange
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            patientService.findPatientById(2L);
        });

        assertEquals("Patient with ID 2 not found", exception.getMessage());
    }

    @Test
    void testDeletePatient_Success() {
        // Arrange
        Long id = 1L;
        when(patientRepository.existsById(id)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(id);

        // Act
        patientService.deletePatient(id);

        // Assert
        verify(patientRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeletePatient_NotFound() {
        // Arrange
        Long id = 99L;
        when(patientRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            patientService.deletePatient(id);
        });

        assertEquals("Patient with ID 99 not found", exception.getMessage());
        verify(patientRepository, never()).deleteById(anyLong());
    }
}