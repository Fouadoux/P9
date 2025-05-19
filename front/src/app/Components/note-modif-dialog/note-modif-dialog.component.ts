import { CommonModule } from '@angular/common';
import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Note } from '../../model/note.model';

/**
 * Dialog component that allows a user to modify an existing medical note.
 * 
 * Uses Angular signals to manage the note comment content in real-time.
 * Emits the updated note via `noteModifier`, and closes the dialog via `close`.
 */
@Component({
  selector: 'app-note-modif-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './note-modif-dialog.component.html',
  styleUrl: './note-modif-dialog.component.css'
})
export class NoteModifDialogComponent {

  /** Input signal representing the note to edit */
  note = input<Note | null>();

  /** Output event emitted with the updated note object */
  noteModifier = output<Note | null>();

  /** Output event emitted to close the dialog */
  close = output<void>();

  /** Signal for binding the comment text inside the textarea */
  text = signal<string>('');

  /**
   * On component init or note update, syncs the text field with the current note's comment.
   */
  constructor() {
    effect(() => {
      if (this.note()) {
        this.text.set(this.note()!.comments);
      }
    });
  }

  /**
   * Saves the edited note by emitting an updated Note object,
   * and closes the dialog.
   */
  save() {
    if (this.text().trim() !== '') {
      if (this.note()) {
        const newNote: Note = {
          ...this.note()!,
          comments: this.text()
        };
        this.noteModifier.emit(newNote);
        this.close.emit();
      }
    }
  }

  /**
   * Updates the text signal when the user types in the textarea.
   * @param event Input event from the textarea.
   */
  updateText(event: Event) {
    const value = (event.target as HTMLTextAreaElement).value;
    this.text.set(value);
  }
}
