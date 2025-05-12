package com.glucovision.patientservice.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucovision.patientservice.dto.PatientDTO;
import com.glucovision.patientservice.model.Patient;
import com.glucovision.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.glucovision.patientservice.model.Gender.FEMALE;
import static com.glucovision.patientservice.model.Gender.MALE;
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


    @BeforeEach
    void clearDatabase() {
        patientRepository.deleteAll();
        patientRepository.flush();

        // Vérification (optionnel)
        long count = patientRepository.count();
        if (count != 0) {
            throw new IllegalStateException("La base n'a pas été correctement vidée ! Reste " + count + " entrées.");
        }
    }

    @Test
    void shouldAllowReadingForInternalService() throws Exception {
        // Test GET avec ROLE_INTERNAL_SERVICE
        mockMvc.perform(get("/api/patients")
                        .with(TestSecurityUtils.mockReader()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowFullAccessForUser() throws Exception {
        // Test POST avec ROLE_USER
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Alice");
        patientDTO.setLastName("Martin");
        patientDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        patientDTO.setGender(FEMALE);
        patientDTO.setAddress("Paris");
        patientDTO.setPhone("0102030405");

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldForbidWritingForInternalService() throws Exception {
        // Test POST avec ROLE_INTERNAL_SERVICE (doit échouer)
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Alice");
        patientDTO.setLastName("Martin");
        patientDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        patientDTO.setGender(FEMALE);
        patientDTO.setAddress("Paris");
        patientDTO.setPhone("0102030405");

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockReader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        // Test PUT avec ROLE_USER
        Patient patient = new Patient();
        patient.setUid(String.valueOf(UUID.randomUUID()));
        patient.setFirstName("Alice");
        patient.setLastName("Martin");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender(FEMALE);
        patient.setAddress("Paris");
        patient.setPhone("0102030405");
        patient.setActive(true);

        Patient saved = patientRepository.save(patient);

        saved.setFirstName("Alicia");
        saved.setAddress("Lyon");

        mockMvc.perform(put("/api/patients/" + saved.getUid())
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.address").value("Lyon"));
    }

    @Test
    void shouldCreatePatient_WithValidData() throws Exception {
        PatientDTO patientDTO = createValidPatientDTO();

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Curie"));
    }

    @Test
    void shouldReturn400_WhenMissingRequiredFields() throws Exception {
        // Test avec firstName manquant
        PatientDTO invalidPatient = createValidPatientDTO();
        invalidPatient.setFirstName(null);

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.status").value(400));
        // Test avec birthDate manquant
        invalidPatient = createValidPatientDTO();
        invalidPatient.setBirthDate(null);

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.birthDate").value("Birth date is required"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldRejectEmptyFirstName() throws Exception {
        PatientDTO patientDTO = createValidPatientDTO();
        patientDTO.setFirstName("   ");

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldRejectInvalidGender() throws Exception {
        String invalidPatientJson = """
    {
        "firstName": "Jean",
        "lastName": "Dupont",
        "birthDate": "1990-01-01",
        "gender": "INVALID_GENDER",
        "address": "123 Rue de Paris",
        "phone": "0123456789"
    }
    """;

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePatient_WithValidData() throws Exception {
        Patient patient = patientRepository.save(createValidPatient());
        PatientDTO updateDTO = createValidPatientDTO();
        updateDTO.setFirstName("UpdatedName");

        mockMvc.perform(put("/api/patients/" + patient.getUid())
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"));
    }

    @Test
    void shouldRejectUpdate_WithInvalidData() throws Exception {
        Patient patient = patientRepository.save(createValidPatient());
        PatientDTO invalidUpdate = createValidPatientDTO();
        invalidUpdate.setLastName(null); // Champ requis manquant

        mockMvc.perform(put("/api/patients/" + patient.getUid())
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreatePatient_WithoutOptionalFields() throws Exception {
        PatientDTO patientDTO = createValidPatientDTO();
        patientDTO.setAddress(null);
        patientDTO.setPhone(null);

        mockMvc.perform(post("/api/patients")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetPatientById() throws Exception {
        Patient patient = patientRepository.save(createValidPatient());

        mockMvc.perform(get("/api/patients/" + patient.getUid())
                        .with(TestSecurityUtils.mockReader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(patient.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(patient.getLastName()));
    }


    /// Méthode utilitaire

    private Patient createValidPatient() {
        return new Patient(
                "Jean",
                "Dupont",
                "1990-01-01",
                MALE,
                "123 Rue de Paris",
                "0123456789"
        );
    }

    private PatientDTO createValidPatientDTO() {
        PatientDTO dto = new PatientDTO();
        dto.setFirstName("Marie");
        dto.setLastName("Curie");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setGender(FEMALE);
        dto.setAddress("456 Science Ave");
        dto.setPhone("0987654321");
        return dto;
    }


}