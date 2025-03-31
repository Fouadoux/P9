package com.glucovision.patientservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.glucovision.patientservice.model.Gender.FEMALE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PatientIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldCreateAndGetPatient() throws Exception {
        // given
        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Martin");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender(FEMALE);
        patient.setAddress("Paris");
        patient.setPhoneNumber("0102030405");

        // when: création
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated());

        // then: récupération
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        // 1. Création d'un patient initial
        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Martin");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender(FEMALE);
        patient.setAddress("Paris");
        patient.setPhoneNumber("0102030405");

        // Enregistre le patient initial via le repo
        Patient saved = patientRepository.save(patient);

        // 2. Prépare les données modifiées
        saved.setFirstName("Alicia");
        saved.setAddress("Lyon");

        // 3. Requête PUT vers /patients/{id}
        mockMvc.perform(put("/patients/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.address").value("Lyon"));
    }
}
