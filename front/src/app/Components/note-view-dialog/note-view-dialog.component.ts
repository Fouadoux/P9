import { Component, input, output } from '@angular/core';
import { Note } from '../../model/note.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-note-view-dialog',
  imports: [CommonModule,FormsModule],
  templateUrl: './note-view-dialog.component.html',
  styleUrl: './note-view-dialog.component.css'
})
export class NoteViewDialogComponent {

note=input<Note|null>(null);
close=output<void>();




}
