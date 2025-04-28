package com.glucovision.patientservice.service;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.exception.PatientNotFoundException;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.glucovision.patientservice.model.Gender.FEMALE;
import static com.glucovision.patientservice.model.Gender.MALE;
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
    public void testGetAllPatients() {
        List<Patient> patients= List.of(
                new Patient(),
                new Patient(),
                new Patient()
                );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result= patientService.getAllPatients();
        assertNotNull(result);
        assertEquals(patients.size(), result.size());
    }

    @Test
    public void testFindByLastName_Found() {
        Patient patient= new Patient();
        when(patientRepository.findByLastName("Doe")).thenReturn(Optional.of(patient));

        Patient result = patientService.findPatientByName("Doe");
        assertNotNull(result);
        assertEquals(patient.getUid(), result.getUid());
        assertEquals(patient.getLastName(), result.getLastName());
    }

    @Test
    public void testFindByLastName_NotFound() {
        when(patientRepository.findByLastName(null)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, ()->patientService.findPatientByName("Doe"));
    }

    @Test
    public void testAddPatient() {
        UUID id = UUID.randomUUID();
        LocalDate birthDate= LocalDate.of(1980, 12, 3);
        PatientDTO patientDTO= new PatientDTO(id,"John","Doe",birthDate,MALE,"place monge","111-222-333");
        Patient patient= new Patient();
        patient.setUid(id);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setGender(MALE);
        patient.setAddress("place monge");
        patient.setAddress("111-222-333");
        patient.setBirthDate(birthDate);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.addPatient(patientDTO);
        assertNotNull(result);
        assertEquals(patient.getUid(), result.getUid());
        assertEquals(patient.getLastName(), result.getLastName());

    }

    @Test
    void testFindPatientByUid_Found() {
        // Arrange
        UUID id = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setUid(id);
        patient.setFirstName("Alice");
        when(patientRepository.findByUid(id)).thenReturn(Optional.of(patient));

        // Act
        Patient result = patientService.findPatientById(id);

        // Assert
        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
    }



    @Test
    void testFindPatientById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(patientRepository.findByUid(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.findPatientById(id);
        });

        assertEquals("Le patient avec l'id "+id+" est inexistant ou inactif.", exception.getMessage());
    }

    @Test
    void testDeletePatient_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.existsByUid(id)).thenReturn(true);
        doNothing().when(patientRepository).deleteByUid(id);

        // Act
        patientService.deletePatient(id);

        // Assert
        verify(patientRepository, times(1)).deleteByUid(id);
    }

    @Test
    void testDeletePatient_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.existsByUid(id)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.deletePatient(id);
        });

        assertEquals("Le patient avec l'id "+id+" est inexistant ou inactif.", exception.getMessage());
        verify(patientRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdatePatient_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        Patient existingPatient = new Patient();
        existingPatient.setUid(id);
        existingPatient.setFirstName("OldFirstName");
        existingPatient.setLastName("OldLastName");
        existingPatient.setBirthDate(birthDate);
        existingPatient.setActive(true);

        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setFirstName("NewFirstName");
        updateDTO.setLastName("NewLastName");
        updateDTO.setBirthDate(birthDate.plusYears(1));
        updateDTO.setActive(false);

        when(patientRepository.findByUid(id)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // Act
        PatientDTO result = patientService.updatePatient(id, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("NewFirstName", result.getFirstName());
        assertEquals("NewLastName", result.getLastName());
        assertEquals(birthDate.plusYears(1), result.getBirthDate());
        assertFalse(result.isActive());
        verify(patientRepository, times(1)).findByUid(id);
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    void testUpdatePatient_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.findByUid(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.updatePatient(id, new PatientDTO());
        });
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testGetAllActivePatients_ReturnsOnlyActivePatients() {
        // Arrange
        Patient activePatient1 = new Patient();
        activePatient1.setActive(true);
        Patient activePatient2 = new Patient();
        activePatient2.setActive(true);
        List<Patient> activePatients = List.of(activePatient1, activePatient2);

        when(patientRepository.findByActiveTrue()).thenReturn(activePatients);

        // Act
        List<Patient> result = patientService.getAllActivePatients();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Patient::isActive));
        verify(patientRepository, times(1)).findByActiveTrue();
    }

    @Test
    void testGetAllActivePatients_ReturnsEmptyListWhenNoActivePatients() {
        // Arrange
        when(patientRepository.findByActiveTrue()).thenReturn(List.of());

        // Act
        List<Patient> result = patientService.getAllActivePatients();

        // Assert
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findByActiveTrue();
    }

    @Test
    void testConvertToDTO() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.now();
        Patient patient = new Patient();
        patient.setUid(id);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setBirthDate(birthDate);
        patient.setGender(MALE);
        patient.setAddress("123 Main St");
        patient.setPhone("555-1234");
        patient.setActive(true);

        // Act
        PatientDTO result = patientService.convertToDTO(patient);

        // Assert
        assertEquals(id, result.getUid());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(birthDate, result.getBirthDate());
        assertEquals(MALE, result.getGender());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("555-1234", result.getPhone());
        assertTrue(result.isActive());
    }

    @Test
    void testConvertToDTOList() {
        // Arrange
        Patient patient1 = new Patient();
        patient1.setUid(UUID.randomUUID());
        Patient patient2 = new Patient();
        patient2.setUid(UUID.randomUUID());
        List<Patient> patients = List.of(patient1, patient2);

        // Act
        List<PatientDTO> result = patientService.convertToDTOList(patients);

        // Assert
        assertEquals(2, result.size());
        assertEquals(patient1.getUid(), result.get(0).getUid());
        assertEquals(patient2.getUid(), result.get(1).getUid());
    }

    @Test
    void testToggleActivePatient_FromActiveToInactive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setUid(id);
        patient.setActive(true);

        when(patientRepository.findByUid(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        // Act
        PatientDTO result = patientService.toggleActivePatient(id);

        // Assert
        assertFalse(result.isActive());
        verify(patientRepository, times(1)).findByUid(id);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testToggleActivePatient_FromInactiveToActive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setUid(id);
        patient.setActive(false);

        when(patientRepository.findByUid(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        // Act
        PatientDTO result = patientService.toggleActivePatient(id);

        // Assert
        assertTrue(result.isActive());
        verify(patientRepository, times(1)).findByUid(id);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testToggleActivePatient_PatientNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.findByUid(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.toggleActivePatient(id);
        });
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testIfActiveOrExistingPatient_ReturnsTrueWhenActive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Patient activePatient = new Patient();
        activePatient.setUid(id);
        activePatient.setActive(true);

        when(patientRepository.findByUid(id)).thenReturn(Optional.of(activePatient));

        // Act
        boolean result = patientService.ifActiveOrExistingPatient(id);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIfActiveOrExistingPatient_ReturnsFalseWhenInactive() {
        // Arrange
        UUID id = UUID.randomUUID();
        Patient inactivePatient = new Patient();
        inactivePatient.setUid(id);
        inactivePatient.setActive(false);

        when(patientRepository.findByUid(id)).thenReturn(Optional.of(inactivePatient));

        // Act
        boolean result = patientService.ifActiveOrExistingPatient(id);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIfActiveOrExistingPatient_ThrowsWhenPatientNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.findByUid(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.ifActiveOrExistingPatient(id);
        });
    }





}