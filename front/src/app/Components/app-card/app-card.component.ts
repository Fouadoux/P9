import { Component, input, output } from '@angular/core';
import { AppUserResponse } from '../../model/appUserResponse.model';
import { Patient } from '../../model/patient.model';
import { Note } from '../../model/note.model';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';

/**
 * Generic card component capable of displaying data for a user, patient, or note.
 * 
 * Based on the `data` input, it dynamically adapts its display and emits relevant actions
 * such as `edit`, `toggle`, `note`, and `deleted`.
 */
@Component({
  selector: 'app-app-card',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatNativeDateModule],
  templateUrl: './app-card.component.html',
  styleUrls: ['./app-card.component.css']
})
export class AppCardComponent {

  /** Input data object, can be of type AppUserResponse, Patient, or Note */
  data = input<AppUserResponse | Patient | Note | null>(null);

  /** Output event emitted when the edit action is triggered */
  edit = output();

  /** Output event emitted when toggling status (e.g., activation) */
  toggle = output();

  /** Output event emitted to access or open notes */
  note = output();

  /** Output event emitted when a deletion is requested */
  deleted = output();

  /**
   * Type guard: checks if the data is a user.
   */
  isAppUser(data: any): data is AppUserResponse {
    return 'email' in data && 'role' in data;
  }

  /**
   * Type guard: checks if the data is a patient.
   */
  isAppPatient(data: any): data is Patient {
    return 'birthDate' in data && 'gender' in data;
  }

  /**
   * Type guard: checks if the data is a note.
   */
  isAppNote(data: any): data is Note {
    return 'comments' in data && 'creationDate' in data && 'modificationDate' in data;
  }

  /** Emits the `note` event to trigger note-related actions */
  goToNotes() {
    this.note.emit();
  }

  /** Emits the `deleted` event (you can implement logic here) */
  goDeleted() {
    this.deleted.emit();
  }

  // Computed properties for type-checking

  get isUser(): boolean {
    return !!this.data() && this.isAppUser(this.data());
  }

  get isPatient(): boolean {
    return !!this.data() && this.isAppPatient(this.data());
  }

  get isNote(): boolean {
    return !!this.data() && this.isAppNote(this.data());
  }

  // Properties extracted from user data

  get email(): string | undefined {
    return this.isUser ? (this.data() as AppUserResponse).email : undefined;
  }

  get role(): string | undefined {
    return this.isUser ? (this.data() as AppUserResponse).role : undefined;
  }

  // Properties extracted from patient data

  get birthDate(): Date | null {
    return this.isPatient ? (this.data() as Patient).birthDate : null;
  }

  get gender(): string | undefined {
    return this.isPatient ? (this.data() as Patient).gender : undefined;
  }

  // Properties extracted from note data

  get comments(): string | undefined {
    return this.isNote ? (this.data() as Note).comments : undefined;
  }

  get creationDate(): string | undefined {
    return this.isNote ? (this.data() as Note).creationDate : undefined;
  }

  get modificationDate(): string | undefined {
    return this.isNote ? (this.data() as Note).modificationDate : undefined;
  }

  // Convenience aliases for usage in template or conditionals

  get person(): AppUserResponse | Patient | null {
    return this.isUser || this.isPatient ? this.data() as AppUserResponse | Patient : null;
  }

  get user(): AppUserResponse | null {
    return this.isUser ? this.data() as AppUserResponse : null;
  }

  get patient(): Patient | null {
    return this.isPatient ? this.data() as Patient : null;
  }

  get noteItem(): Note | null {
    return this.isNote ? this.data() as Note : null;
  }
}
