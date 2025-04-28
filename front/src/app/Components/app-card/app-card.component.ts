import { Component, input, output } from '@angular/core';
import { AppUserResponse } from '../../model/appUserResponse.model';
import { Patient } from '../../model/patient.model';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { Note } from '../../model/note.model';


@Component({
  selector: 'app-app-card',
  imports: [CommonModule, MatIconModule, MatButtonModule,MatNativeDateModule],
  templateUrl: './app-card.component.html',
  styleUrls: ['./app-card.component.css']
})
export class AppCardComponent {
  data=input<AppUserResponse|Patient|Note|null>(null);
  edit= output();
  toggle=output();
  note=output();
  deleted=output();

  isAppUser(data: any): data is AppUserResponse {
    return 'email' in data && 'role' in data;
  }
  
  isAppPatient(data: any): data is Patient {
    return 'birthDate' in data && 'gender' in data;
  }

  isAppNote(data:any):data is Note{
    return 'comments' in data && 'creationDate' in data && 'modificationDate' in data;
  }

  goToNotes() {
    this.note.emit();
  }

  goDeleted(){

  }
  
  get isUser(): boolean {
    return !!this.data() && this.isAppUser(this.data());
  }
  
  get isPatient(): boolean {
    return !!this.data() && this.isAppPatient(this.data());
  }

  get isNote():boolean{
    return !!this.data() && this.isAppNote(this.data());
  }
  
  get email(): string | undefined {
    if (this.isUser) {
      return (this.data() as AppUserResponse).email;
    }
    return undefined;
  }
  
  get role(): string | undefined {
    if (this.isUser) {
      return (this.data() as AppUserResponse).role;
    }
    return undefined;
  }
  
  get birthDate(): Date | null {
    if (this.isPatient) {
      return (this.data() as Patient).birthDate;
    }
    return null;
  }
  
  get gender(): string | undefined {
    if (this.isPatient) {
      return (this.data() as Patient).gender;
    }
    return undefined;
  }

  get comments(): string | undefined{
    if(this.isNote){
      return (this.data() as Note).comments;
    }
    return undefined;
  }

  get creationDate(): string | undefined{
    if(this.isNote){
      return (this.data() as Note).creationDate;
    }
    return undefined;
  }

  get modificationDate(): string | undefined{
    if(this.isNote){
      return (this.data() as Note).modificationDate;
    }
    return undefined;
  }

  get person(): AppUserResponse | Patient | null {
    return this.isUser || this.isPatient ? this.data() as AppUserResponse | Patient : null;
  }

  get user(): AppUserResponse | null {
    return this.isUser && this.data() ? this.data() as AppUserResponse : null;
  }
  
  get patient(): Patient | null {
    return this.isPatient && this.data() ? this.data() as Patient : null;
  }
  
  get noteItem(): Note | null {
    return this.isNote && this.data() ? this.data() as Note : null;
  }
  
  
}
