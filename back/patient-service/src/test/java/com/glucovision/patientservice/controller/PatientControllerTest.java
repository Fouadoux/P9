package com.glucovision.patientservice.controller;

import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.exception.GlobalExceptionHandler;
import com.glucovision.patientservice.exception.PatientNotFoundException;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.glucovision.patientservice.model.Gender.FEMALE;
import static com.glucovision.patientservice.model.Gender.MALE;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Ajoutez votre GlobalExceptionHandler
                .build();
    }

    @Test
    void testDeletePatient_Success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(patientService).deletePatient(id);

        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePatient_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException("Patient with ID " + id + " not found"))
                .when(patientService).deletePatient(id);

        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with ID " + id + " not found"));
    }

    @Test
    void testGetPatientByName_Success() throws Exception {
        String name = "Doe";
        Patient patient = new Patient();
        patient.setLastName(name);

        PatientDTO patientDto = new PatientDTO();
        patientDto.setLastName(name);

        when(patientService.findPatientByName(name)).thenReturn(patient);
        when(patientService.convertToDTO(patient)).thenReturn(patientDto);

        mockMvc.perform(get("/api/patients/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value(name));
    }

    @Test
    void testGetPatientByName_NotFound() throws Exception {
        String name = "Doe";
        when(patientService.findPatientByName(name))
                .thenThrow(new NoSuchElementException("Patient with name " + name + " not found"));

        mockMvc.perform(get("/api/patients/name/{name}", name))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with name " + name + " not found"));
    }

    @Test
    void testGetPatientById_Found() throws Exception {
        UUID id = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setUid(id);
        patient.setFirstName("John");

        PatientDTO patientDto = new PatientDTO();
        patientDto.setFirstName("John");

        when(patientService.findPatientById(id)).thenReturn(patient);
        when(patientService.convertToDTO(patient)).thenReturn(patientDto);

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

  /*  @Test
    void testGetPatientById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(patientService.findPatientById(id))
                .thenThrow(new NoSuchElementException("Patient with ID " + id + " not found"));

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with ID " + id + " not found"));
    }

   */

    @Test
    void testAddPatient_Valid() throws Exception {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUid(UUID.randomUUID());
        patient.setFirstName("Alice");
        patient.setLastName("Doe");
        patient.setGender(FEMALE);
        patient.setActive(true);

        // Utilisez lenient() pour ignorer les avertissements de stubs inutiles
        lenient().when(patientService.addPatient(any(PatientDTO.class))).thenReturn(patient);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "firstName": "Alice",
                    "lastName": "Doe",
                    "birthDate": "1992-10-20",
                    "gender": "FEMALE"
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }


    @Test
    void testAddPatient_Invalid() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "firstName": "",
                        "lastName": "Doe",
                        "birthDate": "1992-10-20",
                        "gender": "FEMALE"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"));
    }

    @Test
    void testCreatePatient_InvalidData() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstName": "",
                            "lastName": "Doe",
                            "birthDate": "1992-10-20",
                            "gender": "FEMALE"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(patientService.findPatientById(id)).thenThrow(new PatientNotFoundException(id));

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPatientsActive() throws Exception {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        Patient patient1 = new Patient();
        patient1.setUid(id1);
        patient1.setFirstName("Alice");
        patient1.setLastName("Doe");
        patient1.setBirthDate(birthDate);
        patient1.setGender(FEMALE);
        patient1.setActive(true);

        Patient patient2 = new Patient();
        patient2.setUid(id2);
        patient2.setFirstName("Bob");
        patient2.setLastName("Smith");
        patient2.setBirthDate(birthDate);
        patient2.setGender(MALE);
        patient2.setActive(true);

        List<Patient> patients = List.of(patient1, patient2);

        PatientDTO patientDTO1 = new PatientDTO();
        patientDTO1.setUid(id1);
        patientDTO1.setFirstName("Alice");
        patientDTO1.setLastName("Doe");
        patientDTO1.setBirthDate(birthDate);
        patientDTO1.setGender(FEMALE);
        patientDTO1.setActive(true);

        PatientDTO patientDTO2 = new PatientDTO();
        patientDTO2.setUid(id2);
        patientDTO2.setFirstName("Bob");
        patientDTO2.setLastName("Smith");
        patientDTO2.setBirthDate(birthDate);
        patientDTO2.setGender(MALE);
        patientDTO2.setActive(true);

        List<PatientDTO> patientDTOs = List.of(patientDTO1, patientDTO2);

        when(patientService.getAllActivePatients()).thenReturn(patients);
        when(patientService.convertToDTOList(patients)).thenReturn(patientDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/patients/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));
    }


    @Test
    void testUpdatePatient_Success() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setFirstName("NewFirstName");
        updateDTO.setLastName("NewLastName");
        updateDTO.setBirthDate(birthDate);
        updateDTO.setGender(FEMALE);
        updateDTO.setActive(true);

        PatientDTO updatedPatient = new PatientDTO();
        updatedPatient.setUid(id);
        updatedPatient.setFirstName("NewFirstName");
        updatedPatient.setLastName("NewLastName");
        updatedPatient.setBirthDate(birthDate);
        updatedPatient.setGender(FEMALE);
        updatedPatient.setActive(true);

        when(patientService.updatePatient(id, updateDTO)).thenReturn(updatedPatient);

        // Act & Assert
        mockMvc.perform(put("/api/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstName": "NewFirstName",
                                "lastName": "NewLastName",
                                "birthDate": "1990-05-15",
                                "gender": "FEMALE",
                                "active": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewFirstName"))
                .andExpect(jsonPath("$.lastName").value("NewLastName"))
                .andExpect(jsonPath("$.birthDate[0]").value(1990))
                .andExpect(jsonPath("$.birthDate[1]").value(5))
                .andExpect(jsonPath("$.birthDate[2]").value(15))
                .andExpect(jsonPath("$.gender").value("FEMALE"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdatePatient_NotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setFirstName("NewFirstName");
        updateDTO.setLastName("NewLastName");
        updateDTO.setBirthDate(birthDate);
        updateDTO.setGender(FEMALE);
        updateDTO.setActive(true);

        when(patientService.updatePatient(id, updateDTO)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstName": "NewFirstName",
                                "lastName": "NewLastName",
                                "birthDate": "1990-05-15",
                                "gender": "FEMALE",
                                "active": true
                            }
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePatient_InvalidData() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/api/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstName": "",
                                "lastName": "NewLastName",
                                "birthDate": "1990-05-15",
                                "gender": "FEMALE",
                                "active": true
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatients() throws Exception {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        Patient patient1 = new Patient();
        patient1.setUid(id1);
        patient1.setFirstName("Alice");
        patient1.setLastName("Doe");
        patient1.setBirthDate(birthDate);
        patient1.setGender(FEMALE);
        patient1.setActive(false);

        Patient patient2 = new Patient();
        patient2.setUid(id2);
        patient2.setFirstName("Bob");
        patient2.setLastName("Smith");
        patient2.setBirthDate(birthDate);
        patient2.setGender(MALE);
        patient2.setActive(true);

        List<Patient> patients = List.of(patient1, patient2);

        PatientDTO patientDTO1 = new PatientDTO();
        patientDTO1.setUid(id1);
        patientDTO1.setFirstName("Alice");
        patientDTO1.setLastName("Doe");
        patientDTO1.setBirthDate(birthDate);
        patientDTO1.setGender(FEMALE);
        patientDTO1.setActive(false);

        PatientDTO patientDTO2 = new PatientDTO();
        patientDTO2.setUid(id2);
        patientDTO2.setFirstName("Bob");
        patientDTO2.setLastName("Smith");
        patientDTO2.setBirthDate(birthDate);
        patientDTO2.setGender(MALE);
        patientDTO2.setActive(true);

        List<PatientDTO> patientDTOs = List.of(patientDTO1, patientDTO2);

        when(patientService.getAllPatients()).thenReturn(patients);
        when(patientService.convertToDTOList(patients)).thenReturn(patientDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));
    }

    @Test
    public void togglePatient_shouldActivateInactivePatientAndReturnDTO() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        // Patient initial (inactif)
        Patient inactivePatient = new Patient();
        inactivePatient.setUid(patientId);
        inactivePatient.setFirstName("Alice");
        inactivePatient.setLastName("Doe");
        inactivePatient.setBirthDate(birthDate);
        inactivePatient.setGender(FEMALE);
        inactivePatient.setActive(false); // Statut initial

        // DTO attendu après toggle (actif)
        PatientDTO expectedDto = new PatientDTO();
        expectedDto.setUid(patientId);
        expectedDto.setFirstName("Alice");
        expectedDto.setLastName("Doe");
        expectedDto.setBirthDate(birthDate);
        expectedDto.setGender(FEMALE);
        expectedDto.setActive(true); // Statut après toggle

        when(patientService.toggleActivePatient(patientId)).thenReturn(expectedDto);

        // Act
        ResponseEntity<PatientDTO> response = patientController.togglePatient(patientId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        PatientDTO responseBody = response.getBody();
        assertNotNull(responseBody);

        // Vérification des propriétés
        assertEquals(patientId, responseBody.getUid());
        assertEquals("Alice", responseBody.getFirstName());
        assertEquals("Doe", responseBody.getLastName());
        assertEquals(birthDate, responseBody.getBirthDate());
        assertEquals(FEMALE, responseBody.getGender());

        assertTrue(responseBody.isActive());

        verify(patientService, times(1)).toggleActivePatient(patientId);
    }
}