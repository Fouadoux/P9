import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-disabled',
  imports: [],
  templateUrl: './account-disabled.component.html',
  styleUrl: './account-disabled.component.css'
})
export class AccountDisabledComponent {

  private router=inject(Router)

  goToLogin(){
    this.router.navigate(['/login'])
  }

}
