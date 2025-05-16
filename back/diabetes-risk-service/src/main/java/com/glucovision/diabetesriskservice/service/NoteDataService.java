package com.glucovision.diabetesriskservice.service;

import com.glucovision.diabetesriskservice.client.NoteClient;
import com.glucovision.diabetesriskservice.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for retrieving medical notes related to a specific patient.
 * <p>
 * Delegates the call to the {@link NoteClient}, which communicates with the Note Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteDataService {

    private final NoteClient noteClient;

    /**
     * Retrieves the list of medical notes for the given patient ID.
     *
     * @param id the unique identifier of the patient
     * @return a list of {@link NoteDto} objects associated with the patient
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public List<NoteDto> getNoteDtoList(String id) {
        log.debug("Fetching notes for patientId={}", id);

        if (id == null || id.trim().isEmpty()) {
            log.warn("Attempted to fetch notes with null or empty patient ID");
            throw new IllegalArgumentException("L'ID du patient ne peut pas être null ou vide");
        }

        List<NoteDto> notes = noteClient.getNoteListe(id);
        log.info("Retrieved {} note(s) for patientId={}", notes.size(), id);

        return notes;
    }
}
