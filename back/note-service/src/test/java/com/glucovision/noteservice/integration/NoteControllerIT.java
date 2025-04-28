package com.glucovision.noteservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucovision.noteservice.client.PatientClient;
import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NoteControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientClient patientClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setup() {
        noteRepository.deleteAll(); // on part sur une base propre à chaque test
    }

    @Test
    void shouldAddNoteToDatabase() throws Exception {
        when(patientClient.isActivePatient(anyString()))
                .thenReturn(true);
        mockMvc.perform(post("/api/notes")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "patientId": "1",
                            "comments": "Intégration test"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comments").value("Intégration test"));

        List<Note> notes = noteRepository.findAll();
        assertThat(notes).hasSize(1);
        assertThat(notes.get(0).getComments()).isEqualTo("Intégration test");
        assertThat(notes.get(0).getPatientId()).isEqualTo("1");
    }



    @Test
    void shouldGetNotesByPatientId() throws Exception {
        // Arrange : insère une note directement dans la base
        Note note = new Note("1", "Consultation intégration", LocalDateTime.now());
        noteRepository.save(note);

        // Act + Assert
        mockMvc.perform(get("/api/notes/patient/1")
                .with(TestSecurityUtils.mockWriter()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].comments").value("Consultation intégration"));
    }

    @Test
    void shouldReturn404IfPatientHasNoNotes() throws Exception {
        mockMvc.perform(get("/api/notes/patient/999")
                        .with(TestSecurityUtils.mockWriter()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateNoteSuccessfully() throws Exception {
        // Arrange
        Note saved = noteRepository.save(new Note("1", "Ancien commentaire", LocalDateTime.now()));
        NoteDto updateDto = new NoteDto("1","Commentaire modifié", saved.getCreationDate() );

        // Act + Assert
        mockMvc.perform(put("/api/notes")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value("1"))
                .andExpect(jsonPath("$.comments").value("Commentaire modifié"));
    }

    @Test
    void shouldDeleteNoteSuccessfully() throws Exception {
        // Arrange
        Note saved = noteRepository.save(new Note("1", "À supprimer", LocalDateTime.now()));
        NoteDto noteDto = new NoteDto("1","À supprimer", saved.getCreationDate());

        // Act + Assert
        mockMvc.perform(delete("/api/notes")
                        .with(TestSecurityUtils.mockWriter())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDto)))
                .andExpect(status().isNoContent());

        assertThat(noteRepository.findAll()).isEmpty();
    }
    @Test
    void shouldReturnNotesForExistingPatient() throws Exception {
        noteRepository.save(new Note("1", "Test patient 1",null));

        mockMvc.perform(get("/api/notes/patient/1")
                        .with(TestSecurityUtils.mockWriter()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comments").value("Test patient 1"));
    }





}
