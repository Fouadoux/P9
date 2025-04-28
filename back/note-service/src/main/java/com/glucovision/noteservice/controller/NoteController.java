package com.glucovision.noteservice.controller;

import com.glucovision.noteservice.dto.NoteDto;
import com.glucovision.noteservice.exception.ErrorResponse;
import com.glucovision.noteservice.service.NoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Data
@AllArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<?> addNote(@Valid @RequestBody NoteDto noteDto) {
        NoteDto note = noteService.addNote(noteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<NoteDto>> findById(@PathVariable String id) {
        List<NoteDto> noteDtoList =noteService.findAllByPatientId(id);

        if (noteDtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(noteDtoList);
    }

    @PutMapping
    public ResponseEntity<?> updateNote(@RequestBody @Valid NoteDto noteDto) {

        try {
            NoteDto updatedNote = noteService.updateNote(noteDto);
            return ResponseEntity.ok(updatedNote);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note non trouvée."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteNote(@RequestBody @Valid NoteDto noteDto) {

        try {
             noteService.deleteNote(noteDto);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note non trouvée."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update")
    public ResponseEntity<?> updateNoteAdmin(@RequestBody @Valid NoteDto noteDto) {

        try {
            NoteDto updatedNote = noteService.updateNoteForAdmin(noteDto);
            return ResponseEntity.ok(updatedNote);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note non trouvée."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> deleteNoteAdmin(@RequestBody @Valid NoteDto noteDto) {

        try {
            noteService.deleteNoteForAdmin(noteDto);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Note non trouvée."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
