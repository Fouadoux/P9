import { Component, effect, input, output } from '@angular/core';
import { Note } from '../../model/note.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-delete-note-dialog',
  imports: [CommonModule],
  templateUrl: './delete-note-dialog.component.html',
  styleUrl: './delete-note-dialog.component.css'
})
export class DeleteNoteDialogComponent {
   noteToDelete=input<Note|null>(null)
   close=output<void>();
   delete=output<Note|null>()

   
}
