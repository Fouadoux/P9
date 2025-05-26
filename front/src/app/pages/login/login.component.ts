import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Login } from '../../model/login.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Component responsible for user login functionality.
 * Manages email/password input using Angular signals and interacts with the AuthService
 * to authenticate the user.
 *
 * Redirects the user after login based on their role.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  /** Email input value (signal-based) */
  email = signal('');

  /** Password input value (signal-based) */
  password = signal('');

  /** Error message shown if login fails */
  errorMessage = signal<string | null>(null);

  /** Angular route and router injection for navigation */
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  /** Injected AuthService to perform the login request */
  private authService = inject(AuthService);

  /**
   * Submits login credentials to the backend.
   * On success, stores auth info in localStorage and redirects based on user role.
   * On failure, displays the appropriate error message.
   */
  login() {
    const emailValue = this.email().trim();
    const passwordValue = this.password().trim();

    if (!emailValue || !passwordValue) {
      this.errorMessage.set("Veuillez remplir tous les champs.");
      return;
    }

    const loginData: Login = {
      email: this.email(),
      password: this.password()
    };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('userEmail', res.email);
        localStorage.setItem('userRole', res.role);
        localStorage.setItem('firstName', res.firstName);
        localStorage.setItem('lastName', res.lastName);

        if (res.role === 'PENDING') {
          this.router.navigate(['/register-success']);
        } else if (res.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/patients']);
        }
      },
    error: (err) => {
  switch (err.error?.error) {
    case 'DISABLED_ACCOUNT':
      this.router.navigate(['/account-disabled']);
      break;
    case 'INVALID_PASSWORD':
      this.errorMessage.set("Mot de passe incorrect.");
      break;
    case 'INVALID_CREDENTIALS':
      this.errorMessage.set("Identifiants incorrects.");
      break;
    default:
      this.errorMessage.set("Une erreur inattendue est survenue.");
      break;
  }
}

    });
  }

  /**
   * Updates the email signal when user types in the email field.
   * @param event Input event from the email field.
   */
  updateEmail(event: Event) {
    this.email.set((event.target as HTMLInputElement).value);
  }

  /**
   * Updates the password signal when user types in the password field.
   * @param event Input event from the password field.
   */
  updatePassword(event: Event) {
    this.password.set((event.target as HTMLInputElement).value);
  }

  /**
   * Navigates to the registration page.
   */
  goToRegister() {
    this.router.navigate(['/register']);
  }
}
