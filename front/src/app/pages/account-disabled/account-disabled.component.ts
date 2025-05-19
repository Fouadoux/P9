import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Component displayed when a user's account has been disabled.
 * Informs the user and provides an option to return to the login page.
 */
@Component({
  selector: 'app-account-disabled',
  imports: [],
  templateUrl: './account-disabled.component.html',
  styleUrl: './account-disabled.component.css'
})
export class AccountDisabledComponent {

  /** Injected Angular Router to handle navigation */
  private router = inject(Router);

  /**
   * Navigates the user back to the login page.
   */
  goToLogin() {
    this.router.navigate(['/login']);
  }
}
