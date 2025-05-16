package com.glucovision.noteservice.controller;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.ErrorResponse;
import com.glucovision.noteservice.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST controller that handles operations related to medical notes.
 * <p>
 * This controller allows users and administrators to:
 * <ul>
 *   <li>Create new medical notes</li>
 *   <li>Retrieve notes by patient ID</li>
 *   <li>Update or delete notes, with role-based restrictions</li>
 * </ul>
 * <p>
 * Admin-specific endpoints bypass standard time-based restrictions.
 */
@Slf4j
@RestController
@Data
@AllArgsConstructor
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Endpoints for managing medical notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Creates a new medical note for a patient.
     *
     * @param noteDto the note to be created
     * @return HTTP 201 with the created note, or 400 if invalid input
     */
    @Operation(summary = "Create a new note for a patient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<?> addNote(@Valid @RequestBody NoteDto noteDto) {
        log.info("[POST] Creating note for patientId={} | Content: {}", noteDto.getPatientId(), noteDto.getComments());
        NoteDto note = noteService.addNote(noteDto);
        log.info("✅ Note created at {}", note.getCreationDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    /**
     * Retrieves all notes associated with a given patient ID.
     *
     * @param id the ID of the patient
     * @return a list of notes
     */
    @Operation(summary = "Get all notes for a specific patient")
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<NoteDto>> findById(
            @Parameter(description = "Patient ID") @PathVariable String id) {
        log.info("[GET] Retrieving notes for patientId={}", id);
        List<NoteDto> noteDtoList = noteService.findAllByPatientId(id);
        log.info("✅ {} note(s) retrieved", noteDtoList.size());
        return ResponseEntity.ok(noteDtoList);
    }

    /**
     * Updates an existing note if it was created less than 24 hours ago.
     *
     * @param noteDto the updated note data
     * @return HTTP 200 if successful, 403 if update is forbidden, 404 if not found
     */
    @Operation(summary = "Update an existing note (for user)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note successfully updated"),
            @ApiResponse(responseCode = "403", description = "Modification not allowed"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @PutMapping
    public ResponseEntity<?> updateNote(@RequestBody @Valid NoteDto noteDto) {
        log.info("[PUT] Updating note for patientId={} | Created at: {}", noteDto.getPatientId(), noteDto.getCreationDate());
        try {
            NoteDto updatedNote = noteService.updateNote(noteDto);
            log.info("✅ Note updated at {}", updatedNote.getModificationDate());
            return ResponseEntity.ok(updatedNote);
        } catch (NoSuchElementException e) {
            log.warn("❌ Note not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note not found."));
        } catch (IllegalStateException e) {
            log.warn("❌ Update forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deletes a note if it was created less than 24 hours ago.
     *
     * @param noteDto the note to be deleted
     * @return HTTP 204 if successful, 403 if forbidden, 404 if not found
     */
    @Operation(summary = "Delete an existing note (for user)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Note successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Deletion not allowed"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteNote(@RequestBody @Valid NoteDto noteDto) {
        log.info("[DELETE] Deleting note for patientId={} | Created at: {}", noteDto.getPatientId(), noteDto.getCreationDate());
        try {
            noteService.deleteNote(noteDto);
            log.info("✅ Note successfully deleted");
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("❌ Note not found for deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note not found."));
        } catch (IllegalStateException e) {
            log.warn("❌ Deletion forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Admin-only: forcibly updates a note, regardless of age.
     *
     * @param noteDto the note to be updated
     * @return HTTP 200 if successful, 403 if forbidden, 404 if not found
     */
    @Operation(summary = "Update a note as administrator")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update")
    public ResponseEntity<?> updateNoteAdmin(@RequestBody @Valid NoteDto noteDto) {
        log.info("[ADMIN PUT] Updating note (admin override) for patientId={} | Created at: {}", noteDto.getPatientId(), noteDto.getCreationDate());
        try {
            NoteDto updatedNote = noteService.updateNoteForAdmin(noteDto);
            log.info("✅ Admin updated note at {}", updatedNote.getModificationDate());
            return ResponseEntity.ok(updatedNote);
        } catch (NoSuchElementException e) {
            log.warn("❌ Note not found for admin update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note not found."));
        } catch (IllegalStateException e) {
            log.warn("❌ Admin update forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Admin-only: forcibly deletes a note, regardless of age.
     *
     * @param noteDto the note to be deleted
     * @return HTTP 204 if successful, 403 if forbidden, 404 if not found
     */
    @Operation(summary = "Delete a note as administrator")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> deleteNoteAdmin(@RequestBody @Valid NoteDto noteDto) {
        log.info("[ADMIN DELETE] Deleting note (admin override) for patientId={} | Created at: {}", noteDto.getPatientId(), noteDto.getCreationDate());
        try {
            noteService.deleteNoteForAdmin(noteDto);
            log.info("✅ Admin successfully deleted note");
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("❌ Note not found for admin deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note not found."));
        } catch (IllegalStateException e) {
            log.warn("❌ Admin deletion forbidden: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
