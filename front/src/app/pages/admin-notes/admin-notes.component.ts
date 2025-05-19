import { Component, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NoteService } from '../../services/note.service';
import { Note } from '../../model/note.model';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { Patient } from '../../model/patient.model';
import { PatientService } from '../../services/patient.service';
import { AppCardComponent } from '../../Components/app-card/app-card.component';

/**
 * Admin component for managing medical notes of a specific patient.
 * Allows adding, editing, and deleting notes, and displays them sorted by date.
 * Also handles loading patient data based on route parameters.
 */
@Component({
  selector: 'app-admin-notes',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule, AppCardComponent],
  templateUrl: './admin-notes.component.html',
  styleUrl: './admin-notes.component.css'
})
export class AdminNotesComponent {

  /** Injected Angular services and dependencies */
  private route       = inject(ActivatedRoute);
  private router      = inject(Router);
  private noteService = inject(NoteService);
  private dialog      = inject(MatDialog);
  private patientSvc  = inject(PatientService);

  /** Signal holding the list of notes */
  notes = signal<Note[]>([]);

  /** Signal containing the patient ID from route parameters */
  patientId = signal<string | null>(this.route.snapshot.paramMap.get('id'));

  /** Signal storing the current patient's details */
  patient = signal<Patient | null>(null);

  /** Signal representing loading state */
  isLoading = signal(true);

  /**
   * Initializes the component by fetching patient details and notes.
   */
  constructor() {
    effect(() => {
      const id = this.patientId();
      if (!id) return;

      this.isLoading.set(true);

      // Fetch patient details
      this.patientSvc.getPatientById(id).subscribe({
        next: p => this.patient.set(p),
        error: () => {
          console.error('Failed to fetch patient');
          this.router.navigate(['/admin/patients']);
        }
      });

      // Fetch notes
      this.refreshNotes();
    });
  }

  /**
   * Retrieves and updates the notes list for the current patient.
   * Notes are sorted in descending order by creation date.
   */
  refreshNotes(): void {
    const id = this.patientId();
    if (!id) return;

    this.noteService.getNotesByPatientId(id).subscribe({
      next: (data: Note[]) => {
        this.notes.set(
          data.sort((a, b) =>
            new Date(b.creationDate ?? '').getTime() -
            new Date(a.creationDate ?? '').getTime()
          )
        );
        this.isLoading.set(false);
      },
      error: () => {
        this.notes.set([]);
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Opens a dialog to add a new note for the current patient.
   * After successful addition, refreshes the notes list.
   */
  onAddNote(): void {
    const dialogRef = this.dialog.open(EditDialogComponent<Note>, {
      data: {
        data: {
          patientId: this.patientId(),
          comments: '',
          creationDate: '',
          modificationDate: ''
        },
        type: 'note',
        title: 'Ajouter une note'
      },
      width: '500px'
    });

    dialogRef.afterClosed().subscribe((result: Note | null) => {
      if (result) {
        this.noteService.addNoteByPatientId(result).subscribe({
          next: () => this.refreshNotes(),
          error: err => console.error('❌ Error adding note', err)
        });
      }
    });
  }

  /**
   * Opens a dialog to edit an existing note.
   * @param note The note to be edited.
   */
  onEditNote(note: Note): void {
    const dialogRef = this.dialog.open(EditDialogComponent<Note>, {
      data: {
        data: note,
        type: 'note',
        title: 'Modifier la note'
      },
      width: '500px'
    });

    dialogRef.afterClosed().subscribe((result: Note | null) => {
      if (result) {
        this.noteService.updateNoteAdmin(result).subscribe({
          next: () => this.refreshNotes(),
          error: err => console.error('❌ Error updating note', err)
        });
      }
    });
  }

  /**
   * Deletes the specified note from the backend and refreshes the list.
   * @param note The note to delete.
   */
  onDeleteNote(note: Note): void {
    this.noteService.deleteNoteAdmin(note).subscribe({
      next: () => this.refreshNotes(),
      error: err => console.error('❌ Error deleting note', err)
    });
  }

  /**
   * Navigates back to the patient list.
   */
  goBack(): void {
    this.router.navigate(['/admin/patients']);
  }
}
