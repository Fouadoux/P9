import { Component, input, output } from '@angular/core';
import { AppUserResponse } from '../../model/appUserResponse.model';
import { Patient } from '../../model/patient.model';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-app-card',
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './app-card.component.html',
  styleUrl: './app-card.component.css'
})
export class AppCardComponent {
  data=input<AppUserResponse|Patient|null>(null)
  edit= output()
  toggle=output()
  note=output()

  isAppUser(data: any): data is AppUserResponse {
    return 'email' in data && 'role' in data;
  }
  
  isAppPatient(data: any): data is Patient {
    return 'birthDate' in data && 'gender' in data;
  }

  goToNotes() {
    this.note.emit();
  }
  
  get isUser(): boolean {
    return !!this.data() && this.isAppUser(this.data());
  }
  
  get isPatient(): boolean {
    return !!this.data() && this.isAppPatient(this.data());
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
  
  get birthDate(): string | undefined {
    if (this.isPatient) {
      return (this.data() as Patient).birthDate;
    }
    return undefined;
  }
  
  get gender(): string | undefined {
    if (this.isPatient) {
      return (this.data() as Patient).gender;
    }
    return undefined;
  }
  
  
  


}
