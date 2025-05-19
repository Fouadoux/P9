import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

/**
 * Component displayed when a route does not match any defined path.
 * 
 * Includes logic to redirect the user based on their role stored in localStorage.
 * Admins are redirected to the admin panel, users to the patient list,
 * and unauthenticated users to the login page.
 */
@Component({
  selector: 'app-not-found',
  imports: [MatCardModule, MatIconModule, CommonModule],
  templateUrl: './not-found.component.html',
  styleUrl: './not-found.component.css'
})
export class NotFoundComponent {

  /** Injected Angular Router to perform navigation */
  private router = inject(Router);

  /**
   * Redirects the user based on their stored role.
   * - ADMIN → /admin
   * - USER → /patients
   * - Unknown/unauthenticated → /login
   */
  redirect() {
    const role = localStorage.getItem('userRole');
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin']);
        break;
      case 'USER':
        this.router.navigate(['/patients']);
        break;
      default:
        this.router.navigate(['/login']);
    }
  }
}
