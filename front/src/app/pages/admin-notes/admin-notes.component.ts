import { Component, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NoteService } from '../../services/note.service';
import { Note } from '../../model/note.model';
import {  MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-admin-notes',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule],
  templateUrl: './admin-notes.component.html',
  styleUrl: './admin-notes.component.css'
})
export class AdminNotesComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router)
  private noteService = inject(NoteService);
  private dialog = inject(MatDialog);

  notes = signal<Note[]>([]);
  patientId = signal<number>(Number(this.route.snapshot.paramMap.get('id')));

  constructor() {
    effect(() => {
      this.noteService.getNotesByPatientId(this.patientId()).subscribe({
        next: (data) => this.notes.set(data),
        error: () => this.notes.set([])
      });
    });
  }

  refreshNotes() {
    this.noteService.getNotesByPatientId(this.patientId()).subscribe({
      next: (data) => this.notes.set(data),
      error: () => this.notes.set([])
    });
  }

  onAddNote() {
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
          error: (err) => console.error('❌ Erreur ajout note', err)
        });
      }
    });
  }
  

  onEditNote(note: Note) {
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
        this.noteService.updateNote(result).subscribe({
          next: () => this.refreshNotes(),
          error: (err) => console.error('❌ Erreur maj note', err)
        });
      }
    });
  }
  

  goBack() {
    this.router.navigate(['/admin/patients']);
  }
}
