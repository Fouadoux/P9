import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RegisterService } from '../../services/register.service';

@Component({
  selector: 'app-register',
  imports: [CommonModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {



  firstName=signal<string>('');
  lastName=signal<string>('');
  email=signal<string>('');
  password=signal<string>('');

  errorMessage=signal<string|null>(null)
  invalidEmail = signal(false);


  private route=inject(ActivatedRoute);
  private router=inject(Router);
  private registerService = inject(RegisterService);

  goToLogin() {
    this.router.navigate(['/login']);
  }


  register() {
    const firstNameValue = this.firstName().trim();
    const lastNameValue = this.lastName().trim();
    const emailValue = this.email().trim();
    const passwordValue = this.password().trim();

    

  
    if (!firstNameValue || !lastNameValue || !emailValue || !passwordValue) {
      this.errorMessage.set("Veuillez remplir tous les champs.");
      return;
    }

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(emailValue)) {
    this.errorMessage.set("Format de l'adresse email invalide.");
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
        this.router.navigate(['/patients']);
      },
      error: () => {
        this.errorMessage.set("Inscription impossible. Vérifie les infos.");
      }
    });
  }

  updateFristname(event: Event) {
    this.firstName.set((event.target as HTMLInputElement).value);
  }
  
  updateLastname(event: Event) {
    this.lastName.set((event.target as HTMLInputElement).value);
  }

  updateEmail(event: Event) {
  const value = (event.target as HTMLInputElement).value;
  this.email.set(value);

  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  this.invalidEmail.set(!emailRegex.test(value));
}
  
  updatePassword(event: Event) {
    this.password.set((event.target as HTMLInputElement).value);
  }

}
