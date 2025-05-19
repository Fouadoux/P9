import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Note } from '../../model/note.model';
import { NoteService } from '../../services/note.service';
import { AddNoteDialogComponent } from '../add-note-dialog/add-note-dialog.component';
import { CommonModule } from '@angular/common';
import { NoteViewDialogComponent } from "../note-view-dialog/note-view-dialog.component";
import { NoteModifDialogComponent } from "../note-modif-dialog/note-modif-dialog.component";
import { DeleteNoteDialogComponent } from '../delete-note-dialog/delete-note-dialog.component';
import { PatientDetailsComponent } from '../../pages/patient-details/patient-details.component';

/**
 * Component for managing and displaying medical notes associated with a patient.
 * 
 * Allows creation, viewing, editing, and deletion of notes. Notes are automatically
 * re-fetched and sorted by creation date upon any update. Also triggers diabetes
 * risk recalculation in the parent component after each change.
 */
@Component({
  selector: 'app-patient-note',
  standalone: true,
  imports: [
    AddNoteDialogComponent,
    CommonModule,
    NoteViewDialogComponent,
    NoteModifDialogComponent,
    DeleteNoteDialogComponent
  ],
  templateUrl: './patient-note.component.html',
  styleUrl: './patient-note.component.css'
})
export class PatientNoteComponent {

  /** Input signal for patient ID (used to fetch notes) */
  patientId = input<string | null>();

  /** Signal holding the list of notes */
  notes = signal<Note[]>([]);

  /** Injected NoteService to perform API operations */
  noteService = inject(NoteService);

  /** Dialog visibility signals */
  showDialog = signal(false);
  showViewDialog = signal(false);
  showModifDialog = signal(false);
  showDeleteDialog = signal(false);

  /** Signals to manage selected note context */
  selectedNote = signal<Note | null>(null);
  noteToEdit = signal<Note | null>(null);
  noteToDelete = signal<Note | null>(null);

  /** Reference to the parent component, used to trigger risk refresh */
  private parent = inject(PatientDetailsComponent);

  /**
   * On component init: fetches all notes for the given patient ID.
   * Sorts notes by creation date (descending).
   */
  constructor() {
    effect(() => {
      const id = this.patientId();
      if (id != null) {
        this.noteService.getNotesByPatientId(id).subscribe({
          next: (data) =>
            this.notes.set(
              data.sort(
                (a, b) =>
                  new Date(b.creationDate ?? '').getTime() -
                  new Date(a.creationDate ?? '').getTime()
              )
            ),
          error: () => this.notes.set([]),
        });
      }
    });
  }

  /**
   * Handles creation of a new note and refreshes the note list + risk score.
   * @param noteData Object containing `patientId` and `comments`.
   */
  handleNoteSubmit(noteData: { patientId: string; comments: string }) {
    this.noteService.addNoteByPatientId(noteData).subscribe({
      next: () => {
        this.showDialog.set(false);
        this.refreshNotes(noteData.patientId);
      },
      error: (err) => console.log('Note creation error', err),
    });
  }

  /**
   * Handles update of an existing note and refreshes the list + risk.
   * @param note The updated note to save.
   */
  handleNoteUpdate(note: Note) {
    this.noteService.updateNote(note).subscribe({
      next: () => {
        this.showModifDialog.set(false);
        this.refreshNotes(note.patientId);
      },
      error: (err) => console.log('Note update error', err)
    });
  }

  /**
   * Handles deletion of a note and refreshes the list + risk.
   * @param note The note to be deleted.
   */
  handleNoteDelete(note: Note) {
    this.noteService.deleteNote(note).subscribe({
      next: () => {
        this.showDeleteDialog.set(false);
        this.refreshNotes(note.patientId);
      },
      error: (err) => console.log('Note deletion error', err)
    });
  }

  /**
   * Determines if a note can be edited based on its creation date (within 24 hours).
   * @param note The note to check.
   * @returns `true` if the note is editable, `false` otherwise.
   */
  canEdit(note: Note): boolean {
    const date = new Date(note.creationDate ?? '');
    const now = new Date();
    const day = 24 * 60 * 60 * 1000;
    return (now.getTime() - date.getTime()) <= day;
  }

  /**
   * Opens the view dialog for a selected note.
   * @param note The note to display.
   */
  openNote(note: Note) {
    this.selectedNote.set(note);
    this.showViewDialog.set(true);
  }

  /**
   * Opens the edit dialog for a selected note.
   * @param note The note to edit.
   */
  editNote(note: Note) {
    this.noteToEdit.set(note);
    this.showModifDialog.set(true);
  }

  /**
   * Opens the delete confirmation dialog for a selected note.
   * @param note The note to delete.
   */
  deleteNote(note: Note) {
    this.noteToDelete.set(note);
    this.showDeleteDialog.set(true);
  }

  /**
   * Utility method to re-fetch and sort notes after a change.
   * Also refreshes diabetes risk level from parent component.
   * @param patientId The patient ID to reload notes for.
   */
  private refreshNotes(patientId: string) {
    this.noteService.getNotesByPatientId(patientId).subscribe({
      next: (data) => {
        this.notes.set(
          data.sort(
            (a, b) =>
              new Date(b.creationDate ?? '').getTime() -
              new Date(a.creationDate ?? '').getTime()
          )
        );
        this.parent.refreshRisk();
      }
    });
  }
}
