import { CommonModule } from '@angular/common';
import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
@Component({
  standalone: true,
  selector: 'app-add-note-dialog',
  imports: [CommonModule,FormsModule],
  templateUrl: './add-note-dialog.component.html',
  styleUrl: './add-note-dialog.component.css'
})
export class AddNoteDialogComponent {

  patientId=input<string|null>(null);
  comment=output<{patientId:string,comments:string}>();
  text=signal<string>('');
  close = output<void>();

  constructor(){

    
  }

  save(){
    if(this.patientId() !==null && this.text().trim()!==''){
      this.comment.emit({patientId:this.patientId()!,comments:this.text().trim()});
      this.close.emit();
    }
  }


}
