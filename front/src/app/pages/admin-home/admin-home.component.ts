import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

/**
 * Admin dashboard landing component.
 * Provides navigation access to key administrative sections via clickable cards or icons.
 */
@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css'
})
export class AdminHomeComponent {

  /** Injected Angular router used for navigation between admin sections */
  private router = inject(Router);

  /**
   * Navigates to the specified route.
   * @param path The relative path to navigate to (e.g. 'admin/users', 'admin/patients').
   */
  goTo(path: string) {
    this.router.navigate([`/${path}`]);
  }
}
