package com.glucovision.diabetesriskservice.client;

import com.glucovision.diabetesriskservice.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client interface for communicating with the Note Service.
 * <p>
 * This client allows the Diabetes Risk Service to retrieve all medical notes
 * associated with a specific patient, identified by their unique ID.
 * </p>
 */
@FeignClient(name = "note-service", url = "${note-service.url}")
public interface NoteClient {

    /**
     * Retrieves all medical notes for the given patient ID.
     *
     * @param id The unique identifier of the patient.
     * @return A list of {@link NoteDto} containing the patient's medical notes.
     */
    @GetMapping("/api/notes/patient/{id}")
    List<NoteDto> getNoteListe(@PathVariable String id);
}
