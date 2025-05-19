import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Application header component displayed across the app.
 * 
 * Shows the full name of the logged-in user (from `localStorage`)
 * and provides a logout button that clears session data and redirects to login.
 */
@Component({
  selector: 'app-app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app-header.component.html',
  styleUrl: './app-header.component.css'
})
export class AppHeaderComponent {

  /** Injected Angular Router for navigation */
  router = inject(Router);

  /** Signal representing the full name of the logged-in user */
  fullName = signal<string>('');

  /**
   * On component initialization, retrieves the user's first and last name from localStorage.
   */
  constructor() {
    const firstName = localStorage.getItem('firstName');
    const lastName = localStorage.getItem('lastName');
    this.fullName.set(`${firstName ?? ''} ${lastName ?? ''}`.trim());
  }

  /**
   * Clears all localStorage session data and navigates to the login page.
   */
  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
