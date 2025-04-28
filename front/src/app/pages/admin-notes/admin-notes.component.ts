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

@Component({
  selector: 'app-admin-notes',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule,AppCardComponent],
  templateUrl: './admin-notes.component.html',
  styleUrl: './admin-notes.component.css'
})
export class AdminNotesComponent {
  private route        = inject(ActivatedRoute);
  private router       = inject(Router);
  private noteService  = inject(NoteService);
  private dialog       = inject(MatDialog);
  private patientSvc   = inject(PatientService);

  notes     = signal<Note[]>([]);
  patientId = signal<string | null>(this.route.snapshot.paramMap.get('id'));
  patient   = signal<Patient|null>(null);
  isLoading = signal(true);

  constructor() {
    effect(() => {
      const id = this.patientId();
      if (!id) return;

      this.isLoading.set(true);
      this.patientSvc.getPatientById(id).subscribe({
        next: p => this.patient.set(p),
        error: () => {
          console.error('Impossible de récupérer le patient');
          this.router.navigate(['/admin/patients']);
        }
      });
      this.refreshNotes();
    });
  }

  refreshNotes(): void {
    const id = this.patientId();
    if (!id) return;

    this.noteService.getNotesByPatientId(id).subscribe({
      next: (data: Note[]) => {
        this.notes.set(
          data.sort(
            (a, b) =>
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
          error: err => console.error('❌ Erreur ajout note', err)
        });
      }
    });
  }

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
          error: err => console.error('❌ Erreur maj note', err)
        });
      }
    });
  }

  onDeleteNote(note: Note): void {
    this.noteService.deleteNoteAdmin(note).subscribe({
      next: () => this.refreshNotes(),
      error: err => console.error('❌ Erreur suppression note', err)
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/patients']);
  }
}
