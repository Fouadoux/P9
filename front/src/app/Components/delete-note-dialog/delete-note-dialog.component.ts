import { Component, effect, input, output } from '@angular/core';
import { Note } from '../../model/note.model';
import { CommonModule } from '@angular/common';

/**
 * Confirmation dialog for deleting a medical note.
 * 
 * Accepts the note to delete via `@input()` and emits two events:
 * - `delete` with the note to confirm deletion
 * - `close` when the dialog is dismissed without action
 */
@Component({
  selector: 'app-delete-note-dialog',
  imports: [CommonModule],
  templateUrl: './delete-note-dialog.component.html',
  styleUrl: './delete-note-dialog.component.css'
})
export class DeleteNoteDialogComponent {

  /** The note to be deleted (passed into the dialog) */
  noteToDelete = input<Note | null>(null);

  /** Emits when the user cancels or closes the dialog */
  close = output<void>();

  /** Emits when the user confirms deletion */
  delete = output<Note | null>();
}
