package com.glucovision.patientservice.controller;

import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.service.PatientService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")  // Charge application-test.properties
public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .build();
    }

    @Test
    void testDeletePatient_Success() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(patientService).deletePatient(id);

        // Act & Assert
        mockMvc.perform(delete("/patients/{id}", id))
                .andExpect(status().isNoContent()); // 204 No Content
    }

    @Test
    void testDeletePatient_NotFound() throws Exception {
        // Arrange
        Long id = 99L;
        doThrow(new NoSuchElementException("Patient with ID " + id + " not found"))
                .when(patientService).deletePatient(id);

        // Act & Assert
        mockMvc.perform(delete("/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with ID 99 not found"));
    }

    @Test
    void testGetPatientByName_Success() throws Exception {
        String name = "Doe";
        Patient patient = new Patient(1L,"Alice",name,"1989-01-01","M");
        when(patientService.findPatientByName(name)).thenReturn(patient);
        mockMvc.perform(get("/patients/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value(name));
    }

    @Test
    void testGetPatientByName_NotFound() throws Exception {
        String name = "Doe";
        doThrow(new NoSuchElementException("Patient with name " + name + " not found"))
        .when(patientService).findPatientByName(name);
        mockMvc.perform(get("/patients/name/{name}", name))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with name " + name + " not found"));

    }

    @Test
    void testGetPatientById_Found() throws Exception {
        // Arrange
        Long id = 1L;
        Patient patient = new Patient(id, "Alice", "Doe", "1992-10-20", "F");
        when(patientService.findPatientById(id)).thenReturn(patient);

        // Act & Assert
        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testGetPatientById_NotFound() throws Exception {
        // Arrange
        Long id = 99L;
        when(patientService.findPatientById(id)).thenThrow(new NoSuchElementException("Patient with ID " + id + " not found"));

        // Act & Assert
        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Patient with ID 99 not found"));
    }

    @Test
    void testAddPatient_Valid() throws Exception {
        // Arrange
        Patient patient = new Patient(null, "Alice", "Doe", "1992-10-20", "F");
        when(patientService.addPatient(any(Patient.class))).thenReturn(patient);

        // Act & Assert
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "firstName": "Alice",
                    "lastName": "Doe",
                    "birthDate": "1992-10-20",
                    "gender": "F"
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testAddPatient_Invalid() throws Exception {
        // JSON invalide (champ `firstName` vide)
        String invalidPatientJson = """
        {
            "firstName": "",
            "lastName": "Doe",
            "birthDate": "1992-10-20",
            "gender": "F"
        }
        """;

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").value("First name is required"));
    }

}