import { CommonModule } from '@angular/common';
import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

/**
 * Dialog component for adding a new medical note.
 *
 * Accepts a patient ID via `@input()`, and emits a `comment` event
 * with the note content. Closes itself on successful submission.
 */
@Component({
  standalone: true,
  selector: 'app-add-note-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-note-dialog.component.html',
  styleUrl: './add-note-dialog.component.css'
})
export class AddNoteDialogComponent {

  /** The ID of the patient the note is associated with */
  patientId = input<string | null>(null);

  /** Emits the new note data when saved */
  comment = output<{ patientId: string; comments: string }>();

  /** Emits when the dialog is closed (manually or after save) */
  close = output<void>();

  /** Reactive signal holding the comment input content */
  text = signal<string>('');

  constructor() {}

  /**
   * Validates input and emits the new note data.
   * Also triggers dialog closure.
   */
  save() {
    if (this.patientId() !== null && this.text().trim() !== '') {
      this.comment.emit({
        patientId: this.patientId()!,
        comments: this.text().trim()
      });
      this.close.emit();
    }
  }
}
