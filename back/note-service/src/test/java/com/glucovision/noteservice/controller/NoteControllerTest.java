package com.glucovision.noteservice.controller;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.service.NoteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(NoteController.class)
@Import(NoteControllerTest.MockConfig.class)
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteService noteService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public NoteService noteService() {
            return Mockito.mock(NoteService.class);
        }
    }

                            //addNote
    @Test
    void TestAddNote() throws Exception {
        NoteDto outputDto = new NoteDto(1L, "Test comment", LocalDateTime.now());

        when(noteService.addNote(any(NoteDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "patientId": 1,
                              "comments": "Test comment"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.comments").value("Test comment"));
    }

    @Test
    void testAddNote_fail_NoComment() throws Exception {
        String invalidJson = """
        {
          "patientId": 1,
          "comments": ""
        }
        """;

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comments").value("Comment is required"));
    }

    @Test
    void testAddNote_fail_NoPatientId() throws Exception {
        String invalidJson = """
        {
          "comments": "test"
        }
        """;

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.patientId").value("PatientId is required"));
    }


                            //find note

    @Test
    void testFindByPatientId_success() throws Exception {
        NoteDto noteDto1 = new NoteDto(1L, "Test comment 1", LocalDateTime.now());
        NoteDto noteDto2 = new NoteDto(1L, "Test comment 2", LocalDateTime.now());

        List<NoteDto> noteDtos = Arrays.asList(noteDto1, noteDto2);

        when(noteService.findAllByPatientId(1L)).thenReturn(noteDtos);

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].comments").value("Test comment 1"))
                .andExpect(jsonPath("$[1].comments").value("Test comment 2"));

    }

    @Test
    void testFindByPatientId_fail_NotFound() throws Exception {

        Long nonExistentId = 99L;
        when(noteService.findAllByPatientId(nonExistentId))
                .thenThrow(new NoSuchElementException("No notes found for patientId: " + nonExistentId));

        mockMvc.perform(get("/notes/patient/{patientId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No notes found for patientId: 99"));

    }

                                        // Update
                                        @Test
                                        void testUpdateNote_Success() throws Exception {
                                            NoteDto updatedNote = new NoteDto(1L, "updated comment", LocalDateTime.now());

                                            when(noteService.updateNote(anyLong(), any(LocalDateTime.class), anyString()))
                                                    .thenReturn(updatedNote);

                                            mockMvc.perform(put("/notes")
                                                            .param("patientId", "1")
                                                            .param("creationDate", "2025-03-27T10:00:00")
                                                            .param("comments", "updated comment"))
                                                    .andExpect(status().isOk())
                                                    .andExpect(jsonPath("$.comments").value("updated comment"));
                                        }

    @Test
    void testUpdateNote_NotFound() throws Exception {
        when(noteService.updateNote(anyLong(), any(LocalDateTime.class), anyString()))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(put("/notes")
                        .param("patientId", "1")
                        .param("creationDate", "2025-03-27T10:00:00")
                        .param("comments", "new comment"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Note non trouvée."));
    }

    @Test
    void testUpdateNote_TooOld() throws Exception {
        when(noteService.updateNote(anyLong(), any(LocalDateTime.class), anyString()))
                .thenThrow(new IllegalStateException("Modification interdite"));

        mockMvc.perform(put("/notes")
                        .param("patientId", "1")
                        .param("creationDate", "2025-03-25T10:00:00")
                        .param("comments", "new comment"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Modification interdite"));
    }

                                    //delete

    @Test
    void testDeleteNote_Success() throws Exception {
        doNothing().when(noteService).deleteNote(anyLong(), any(LocalDateTime.class));

        mockMvc.perform(delete("/notes")
                        .param("patientId", "1")
                        .param("creationDate", "2025-03-27T10:00:00"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteNote_NotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(noteService).deleteNote(anyLong(), any(LocalDateTime.class));

        mockMvc.perform(delete("/notes")
                        .param("patientId", "1")
                        .param("creationDate", "2025-03-27T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Note non trouvée."));
    }

    @Test
    void testDeleteNote_TooOld() throws Exception {
        doThrow(new IllegalStateException("Suppression interdite")).when(noteService)
                .deleteNote(anyLong(), any(LocalDateTime.class));

        mockMvc.perform(delete("/notes")
                        .param("patientId", "1")
                        .param("creationDate", "2025-03-25T10:00:00"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Suppression interdite"));
    }

}