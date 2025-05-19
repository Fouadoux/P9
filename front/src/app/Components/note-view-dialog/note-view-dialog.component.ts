import { Component, input, output } from '@angular/core';
import { Note } from '../../model/note.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Dialog component used to display the full content of a medical note.
 * 
 * Receives the note to display via `@input()`, and emits a `close` event
 * when the user closes the dialog.
 */
@Component({
  selector: 'app-note-view-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './note-view-dialog.component.html',
  styleUrl: './note-view-dialog.component.css'
})
export class NoteViewDialogComponent {

  /** The note to be displayed in the dialog */
  note = input<Note | null>(null);

  /** Event emitted when the dialog is closed */
  close = output<void>();
}
