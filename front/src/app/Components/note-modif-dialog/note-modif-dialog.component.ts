import { CommonModule } from '@angular/common';
import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Note } from '../../model/note.model';

@Component({
  selector: 'app-note-modif-dialog',
  imports: [CommonModule,FormsModule],
  templateUrl: './note-modif-dialog.component.html',
  styleUrl: './note-modif-dialog.component.css'
})
export class NoteModifDialogComponent {

  note=input<Note|null>()
  noteModifier=output<Note|null>()
  close=output<void>()
  text=signal<string>('')

  constructor() {
    effect(() => {
      if (this.note()) {
        this.text.set(this.note()!.comments);
      }
    });
  }

 
  
  save(){
    if(this.text().trim() !=''){
      if (this.note()){
        const newNote: Note={ 
          ...this.note()!,
          comments:this.text()
        };
        this.noteModifier.emit(newNote);
        this.close.emit();
      }
    }
  }

  updateText(event: Event) {
    const value = (event.target as HTMLTextAreaElement).value;
    this.text.set(value);
  }
  
}
