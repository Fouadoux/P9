import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Login } from '../../model/login.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email=signal('')
  password=signal('')
  errorMessage=signal<string|null>(null)

  private route=inject(ActivatedRoute);
  private router=inject(Router);
  private authService = inject(AuthService);


  login(){
    const emailValue=this.email().trim()
    const passwordValue=this.password().trim()

    if(!emailValue || !passwordValue){
      this.errorMessage.set("Veuillez remplir tous les champs.");
    }

    const loginData = {
      email: this.email(),
      password: this.password()
    };

    this.authService.login(loginData).subscribe({
      next:(res)=>{
        localStorage.setItem('token',res.token);
        localStorage.setItem('userEmail', res.email);
        localStorage.setItem('userRole', res.role);
        localStorage.setItem('firstName', res.firstName);
        localStorage.setItem('lastName', res.lastName);

        if(res.role==='PENDING'){
          this.router.navigate(['/register-success']);
          return;
        }
        if(res.role==='ADMIN'){
          this.router.navigate(['/admin']);
          return;
        }
        this.router.navigate(['/patients']);
      },
      error: (err)=>{  
      if (err.error?.error === 'DISABLED_ACCOUNT') {
        this.router.navigate(['/account-disabled']);
      }else if (err.error?.error === 'INVALID_PASSWORD') {
        this.errorMessage.set("Mot de passe incorrects.")
      }else
        this.errorMessage.set("Identifiants incorrects.")
      }
    });
  }

  updateEmail(event: Event) {
    this.email.set((event.target as HTMLInputElement).value);
  }
  
  updatePassword(event: Event) {
    this.password.set((event.target as HTMLInputElement).value);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}
