package com.glucovision.noteservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucovision.noteservice.model.Note;
import com.glucovision.noteservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NoteControllerIT {
    @Autowired
    private MockMvc mockMvc;

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
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "patientId": 1,
                            "comments": "Intégration test"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comments").value("Intégration test"));

        List<Note> notes = noteRepository.findAll();
        assertThat(notes).hasSize(1);
        assertThat(notes.get(0).getComments()).isEqualTo("Intégration test");
        assertThat(notes.get(0).getPatientId()).isEqualTo(1L);
    }

    @Test
    void shouldGetNotesByPatientId() throws Exception {
        // Arrange : insère une note directement dans la base
        Note note = new Note(1L, "Consultation intégration", LocalDateTime.now());
        noteRepository.save(note);

        // Act + Assert
        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(1))
                .andExpect(jsonPath("$[0].comments").value("Consultation intégration"));
    }

    @Test
    void shouldReturn404IfPatientHasNoNotes() throws Exception {
        mockMvc.perform(get("/notes/patient/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateNoteSuccessfully() throws Exception {
        // Arrange
        Note saved = noteRepository.save(new Note(1L, "Ancien commentaire", LocalDateTime.now()));
        LocalDateTime creationDate = saved.getCreationDate();

        // Act + Assert
        mockMvc.perform(put("/notes")
                        .param("patientId", "1")
                        .param("creationDate", creationDate.toString())
                        .param("comments", "Commentaire modifié"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").value("Commentaire modifié"));
    }

    @Test
    void shouldDeleteNoteSuccessfully() throws Exception {
        // Arrange
        Note saved = noteRepository.save(new Note(1L, "À supprimer", LocalDateTime.now()));
        LocalDateTime creationDate = saved.getCreationDate();

        // Act + Assert
        mockMvc.perform(delete("/notes")
                        .param("patientId", "1")
                        .param("creationDate", creationDate.toString()))
                .andExpect(status().isNoContent());

        assertThat(noteRepository.findAll()).isEmpty();
    }

    @Test
    void shouldReturnNotesForExistingPatient() throws Exception {
        // Préparer les données
        noteRepository.save(new Note(1L, "Test patient 1",null));

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comments").value("Test patient 1"));
    }



}
