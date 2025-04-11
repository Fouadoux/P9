import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-app-header',
  imports: [CommonModule],
  templateUrl: './app-header.component.html',
  styleUrl: './app-header.component.css'
})
export class AppHeaderComponent {

  router=inject(Router)
  fullName = signal<string>('');

constructor() {
  const firstName = localStorage.getItem('firstName');
  const lastName = localStorage.getItem('lastName');
  this.fullName.set(`${firstName ?? ''} ${lastName ?? ''}`.trim());
}

logout() {
  localStorage.clear();
  this.router.navigate(['/login']);
}



}
