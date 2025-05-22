import { Component, effect, inject, signal } from '@angular/core';
import { appUserService } from '../../services/appUser.service';
import { AppUserResponse } from '../../model/appUserResponse.model';
import { CommonModule } from '@angular/common';
import { AppCardComponent } from '../../Components/app-card/app-card.component';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../../Components/search-bar/search-bar.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

/**
 * Admin component for managing application users.
 * Allows administrators to view, edit, and toggle the active status of users.
 * 
 * Uses Angular signals and Material dialogs for a reactive and modern UX.
 */
@Component({
  selector: 'app-admin-user-management',
  imports: [CommonModule, AppCardComponent, MatDialogModule,SearchBarComponent,MatProgressSpinnerModule,MatIconModule,MatButtonModule],
  templateUrl: './admin-user-management.component.html',
  styleUrl: './admin-user-management.component.css'
})
export class AdminUserManagementComponent {

  /** Injected service for user-related backend operations */
  private appUserService = inject(appUserService);

  /** Signal holding the list of all users */
  users = signal<AppUserResponse[]>([]);

  /** Signal indicating loading state */
  isLoading = signal<boolean>(true);

  /** Reactive form builder */
  public fb = inject(FormBuilder);

  /** Currently selected user for editing */
  selectedUser = signal<AppUserResponse | null>(null);

  /** Whether the edit dialog is shown */
  showEditDialog = signal(false);

  /** Injected Material Dialog service */
  private dialog = inject(MatDialog);

  /** Angular Router for navigation */
  private router = inject(Router);

  /** Re-export Angular Validators for template use */
  validators = Validators;

  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  searchQuery = signal('');

  /**
   * Constructor triggers data loading for all users using an effect.
   */
  constructor() {
  effect(() => {
    this.fetchUsers();
  });
}

private fetchUsers(): void {
  this.isLoading.set(true);
  const page = this.currentPage();
  const size = this.pageSize();
  const name = this.searchQuery().trim();

  const request$ = name
    ? this.appUserService.searchUsers(name, page, size)
    : this.appUserService.allUserPaginated(page, size);

  request$.subscribe({
    next: (response) => {
      this.users.set(response.content);
      this.totalPages.set(response.totalPages);
      this.isLoading.set(false);
    },
    error: (err) => {
      console.error('Erreur chargement patients', err);
      this.isLoading.set(false);
    }
  });
}

onSearch(query: string) {
  this.searchQuery.set(query);
  this.currentPage.set(0); // reset pagination
  this.fetchUsers();
}



  /**
   * Opens a dialog to edit the selected user.
   * @param user The user to edit.
   */
  onEditUser(user: AppUserResponse) {
    const dialogRef = this.dialog.open(EditDialogComponent<AppUserResponse>, {
      data: {
        data: user,
        type: 'user',
        title: 'Modifier un utilisateur'
      },
      width: '500px'
    });

    dialogRef.afterClosed().subscribe((result: AppUserResponse | null) => {
      if (result) {
        this.handleUserSave(result);
      }
    });
  }

  /**
   * Handles the user update process after the edit dialog is submitted.
   * Updates the local state with the updated user.
   * @param updatedUser The updated user object returned from the dialog.
   */
  handleUserSave(updatedUser: AppUserResponse) {
    this.appUserService.updateUser(updatedUser).subscribe({
      next: (res) => {
        const users = this.users().map(u => u.id === res.id ? res : u);
        this.users.set(users);
        this.showEditDialog.set(false);
      },
      error: (err) => {
        console.error('❌ Error updating user', err);
      }
    });
  }

  /**
   * Toggles the active/inactive status of a user.
   * @param user The user whose status should be toggled.
   */
  handleToggleActive(user: AppUserResponse) {
    this.appUserService.toggleActiveUser(user.id).subscribe({
      next: (res) => {
        const users = this.users().map(u => u.id === res.id ? res : u);
        this.users.set(users);
      },
      error: (err) => {
        console.error('❌ Error toggling user status', err);
      }
    });
  }

  /**
   * Navigates to a specified route path.
   * @param path The route to navigate to (e.g., 'dashboard').
   */
  goTo(path: string) {
    this.router.navigate([`/${path}`]);
  }

   goToPage(page: number): void {
  if (page >= 0 && page < this.totalPages()) {
    this.currentPage.set(page);
    this.fetchUsers();
  }
}
}
