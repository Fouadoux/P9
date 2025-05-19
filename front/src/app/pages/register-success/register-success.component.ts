import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

/**
 * Component displayed after successful user registration.
 * Shows a confirmation message and allows navigation to other parts of the application.
 *
 * Typically used as a landing page after the register form is submitted.
 */
@Component({
  selector: 'app-register-success',
  imports: [MatCardModule, MatIconModule, CommonModule],
  templateUrl: './register-success.component.html',
  styleUrl: './register-success.component.css'
})
export class RegisterSuccessComponent {

  /** Angular Router used to navigate to other routes after registration */
  private router = inject(Router);

  /**
   * Navigates to the specified path within the application.
   * @param path The relative route to navigate to (e.g., 'login', 'home').
   */
  goTo(path: string) {
    this.router.navigate([`/${path}`]);
  }
}
