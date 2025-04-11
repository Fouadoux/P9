import { Component, input, output, signal, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppUserResponse } from '../../model/appUserResponse.model';

@Component({
  selector: 'app-user-edit-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-edit-dialog.component.html',
  styleUrl: './user-edit-dialog.component.css'
})
export class UserEditDialogComponent {
  private fb = inject(FormBuilder);

  user = input<AppUserResponse | null>(null);
  close = output<void>();
  save = output<AppUserResponse>();

  form = signal<FormGroup | null>(null);

  constructor() {
    effect(() => {
      const u = this.user();
      if (u) {
        this.form.set(this.fb.group({
          firstName: [u.firstName, Validators.required],
          lastName: [u.lastName, Validators.required],
          email: [u.email, [Validators.required, Validators.email]],
          role: [u.role, Validators.required],
          active: [u.active]
        }));
      }
    });
  }

  submit() {
    const f = this.form();
    const currentUser = this.user();
  
    if (!f || f.invalid || !currentUser) return;
  
    const updatedUser: AppUserResponse = {
      ...currentUser,
      ...f.value
    };
  
    this.save.emit(updatedUser);
    this.close.emit();
  }
  
}
