import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RegisterService } from '../../services/register.service';
import { FormsModule } from '@angular/forms';

/**
 * Component responsible for handling user registration.
 * Includes form state management using Angular signals and validation logic.
 * 
 * On successful registration, the user is redirected to a success confirmation page.
 */
@Component({
  selector: 'app-register',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  /** Regex pattern for validating email format */
  private emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  /** Regex pattern for validating strong passwords: at least 9 characters with one special character */
  private passwordRegex = /^(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{9,}$/;


  /** Signals used to track input values for the registration form */
  firstName = signal<string>('');
  lastName = signal<string>('');
  email = signal<string>('');
  password = signal<string>('');

  /** Error message displayed on validation or API errors */
  errorMessage = signal<string | null>(null);

  /** Signal used to track whether the email format is invalid */
  invalidEmail = signal(false);

  /** Injected Angular services for routing and registration */
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private registerService = inject(RegisterService);

  /**
   * Utility method to extract the value from an HTML input event.
   * @param event The input event.
   * @returns The input value as a string.
   */
  private getInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  /**
   * Navigates the user to the login page.
   */
  goToLogin() {
    this.router.navigate(['/login']);
  }

  /**
   * Submits the registration form after validation.
   * Saves the token and user information in localStorage and redirects on success.
   * Displays an error message if the registration fails.
   */
  register() {
    const firstNameValue = this.firstName().trim();
    const lastNameValue = this.lastName().trim();
    const emailValue = this.email().trim();
    const passwordValue = this.password().trim();

    if (!firstNameValue || !lastNameValue || !emailValue || !passwordValue) {
      this.errorMessage.set("Veuillez remplir tous les champs.");
      return;
    }

    if (!this.emailRegex.test(emailValue)) {
      this.errorMessage.set("Format de l'adresse email invalide.");
      return;
    }

    if (!this.passwordRegex.test(passwordValue)) {
      this.errorMessage.set("Le mot de passe doit contenir au moins 9 caractères et un caractère spécial.");
      return;
    }

    const registerData = {
      firstName: firstNameValue,
      lastName: lastNameValue,
      email: emailValue,
      password: passwordValue
    };

    this.registerService.register(registerData).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('userEmail', res.email);
        localStorage.setItem('userRole', res.role);
        this.router.navigate(['/register-success']);
      },
      error: () => {
        this.errorMessage.set("Inscription impossible. Vérifie les infos.");
      }
    });
  }

  /**
   * Updates the first name signal with the new input value.
   * @param event Input event from the first name field.
   */
  updateFirstName(event: Event) {
    this.firstName.set(this.getInputValue(event));
  }

  /**
   * Updates the last name signal with the new input value.
   * @param event Input event from the last name field.
   */
  updateLastName(event: Event) {
    this.lastName.set(this.getInputValue(event));
  }

  /**
   * Updates the email signal and checks for email format validity.
   * @param event Input event from the email field.
   */
  updateEmail(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.email.set(value);
    this.invalidEmail.set(!this.emailRegex.test(value));
  }

  /**
   * Updates the password signal with the new input value.
   * @param event Input event from the password field.
   */
  updatePassword(event: Event) {
    this.password.set(this.getInputValue(event));
  }
}
