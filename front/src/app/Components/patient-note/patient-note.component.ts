import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Note } from '../../model/note.model';
import { NoteService } from '../../services/note.service';
import { AddNoteDialogComponent } from '../add-note-dialog/add-note-dialog.component';
import { CommonModule } from '@angular/common';
import { NoteViewDialogComponent } from "../note-view-dialog/note-view-dialog.component";
import { NoteModifDialogComponent } from "../note-modif-dialog/note-modif-dialog.component";
import { DeleteNoteDialogComponent } from '../delete-note-dialog/delete-note-dialog.component';

@Component({
  selector: 'app-patient-note',
  imports: [AddNoteDialogComponent, CommonModule, NoteViewDialogComponent, NoteModifDialogComponent,DeleteNoteDialogComponent],
  templateUrl: './patient-note.component.html',
  styleUrl: './patient-note.component.css'
})
export class PatientNoteComponent {

  patientId=input<number|null>();
  notes=signal<Note[]>([]);
  noteService=inject(NoteService);
  showDialog =signal(false);
  showViewDialog = signal(false);
  selectedNote = signal<Note | null>(null)
  showModifDialog = signal(false);
  noteToEdit = signal<Note | null>(null);
  showDeleteDialog=signal(false);
  noteToDelete=signal<Note|null>(null);

  constructor(){
    
    effect(()=>{
      const id = this.patientId();
      if (id != null) {
        this.noteService.getNotesByPatientId(id).subscribe({
          next:(data) => this.notes.set(data.sort((a, b) => new Date(b.creationDate?? '').getTime() - new Date(a.creationDate?? '').getTime())),
          error:()=> this.notes.set([]),
        });
      }
    });
  }

  handleNoteSubmit(noteData:{patientId:number,comments:string}){
    this.noteService.addNoteByPatientId(noteData).subscribe({
      next:() => {
        this.showDialog.set(false);
        this.noteService.getNotesByPatientId(noteData.patientId).subscribe({
          next:(data)=>this.notes.set(
            data.sort((a, b) => new Date(b.creationDate?? '').getTime() - new Date(a.creationDate?? '').getTime())          
          )});
      },
      error:(err)=>console.log('Erreur ajout de note',err)
    })
  }

  handleNoteUpdate(note: Note) {
    this.noteService.updateNote(note).subscribe({
      next: () => {
        this.showModifDialog.set(false);
        this.noteService.getNotesByPatientId(note.patientId).subscribe({
          next: (data) =>
            this.notes.set(
              data.sort(
                (a, b) =>
                  new Date(b.creationDate ?? '').getTime() -
                  new Date(a.creationDate ?? '').getTime()
              )
            )
        });
      },
      error: (err) => console.log('Erreur de modification de note', err)
    });
  }

  handleNoteDelete(note:Note){
    this.noteService.deleteNote(note).subscribe({
      next: () => {
        this.showDeleteDialog.set(false);
        this.noteService.getNotesByPatientId(note.patientId).subscribe({
          next: (data) =>
            this.notes.set(
              data.sort(
                (a, b) =>
                  new Date(b.creationDate ?? '').getTime() -
                  new Date(a.creationDate ?? '').getTime()
              )
            )
        });
      },
      error: (err) => console.log('Erreur de modification de note', err)
    });
  }



  
  canEdit(note: Note): boolean {
    const date = new Date(note.creationDate ?? '');
    const now = new Date();
    const day = 24 * 60 * 60 * 1000;
  
    return (now.getTime() - date.getTime()) <= day;
  }

  openNote(note:Note){
    this.selectedNote.set(note);
    this.showViewDialog.set(true);
  }

  editNote(note: Note) {
    this.noteToEdit.set(note);
    this.showModifDialog.set(true);
  }

  deleteNote(note:Note){
    this.noteToDelete.set(note)
    this.showDeleteDialog.set(true)
  }

}
