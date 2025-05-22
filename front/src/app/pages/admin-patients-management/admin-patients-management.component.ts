import { Component, effect, inject, signal } from '@angular/core';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../model/patient.model';
import { AppCardComponent } from "../../Components/app-card/app-card.component";
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators } from '@angular/forms';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../../Components/search-bar/search-bar.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

/**
 * Admin component responsible for managing patients in the application.
 * Allows administrators to view all patients, edit their information,
 * toggle activation status, and navigate to their medical notes.
 */
@Component({
  selector: 'app-admin-patients-management',
  imports: [CommonModule, AppCardComponent, 
    MatDialogModule,SearchBarComponent,
    MatProgressSpinnerModule,MatButtonModule,MatIconModule],
  templateUrl: './admin-patients-management.component.html',
  styleUrl: './admin-patients-management.component.css'
})
export class AdminPatientsManagementComponent {

  /** Injected patient service for backend communication */
  private patientService = inject(PatientService);

  /** Signal holding all patients (active and inactive) */
  patients = signal<Patient[]>([]);

  /** Signal indicating loading state */
  isLoading = signal<boolean>(true);

  /** Angular FormBuilder (in case forms are needed) */
  public fb = inject(FormBuilder);

  /** Currently selected patient for editing */
  selectedPatient = signal<Patient | null>(null);

  /** Controls visibility of the edit dialog */
  showEditDialog = signal(false);

  /** Angular Material dialog service */
  private dialog = inject(MatDialog);

  /** Angular router for navigation */
  private router = inject(Router);

  /** Validators exposed for template-driven forms (if needed) */
  validators = Validators;

  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  searchQuery = signal('');

  /**
   * Constructor sets up effect to fetch all patients on component initialization.
   */
  constructor() {
  effect(() => {
    this.fetchPatients();
  });
}

 private fetchPatients(): void {
  this.isLoading.set(true);
  const page = this.currentPage();
  const size = this.pageSize();
  const name = this.searchQuery().trim();

  const request$ = name
    ? this.patientService.searchPatientsByName(name, page, size)
    : this.patientService.getPatientsPaginated(page, size);

  request$.subscribe({
    next: (response) => {
      this.patients.set(response.content);
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
  this.fetchPatients();
}

  /**
   * Opens a modal dialog to edit the given patient.
   * @param patient The patient to edit.
   */
  onEditPatient(patient: Patient) {
    const dialogRef = this.dialog.open(EditDialogComponent<Patient>, {
      data: {
        data: patient,
        type: 'patient',
        title: 'Modifier un patient'
      },
      width: '500px'
    });

    dialogRef.afterClosed().subscribe((result: Patient | null) => {
      if (result) {
        this.handlePatientSave(result);
      }
    });
  }

  /**
   * Toggles the active status of a patient.
   * @param patient The patient whose status should be changed.
   */
  handleToggleActive(patient: Patient) {
    this.patientService.toggleActivePatient(patient.uid).subscribe({
      next: (res) => {
        const updated = this.patients().map(p =>
          p.uid === res.uid ? res : p
        );
        this.patients.set(updated);
      },
      error: (err) => console.error('Error toggling patient status', err)
    });
  }

  /**
   * Sends updated patient data to the backend and updates local state.
   * @param updatedPatient The updated patient object.
   */
  handlePatientSave(updatedPatient: Patient) {
    this.patientService.updatePatient(updatedPatient).subscribe({
      next: (res) => {
        const updated = this.patients().map(p =>
          p.uid === res.uid ? res : p
        );
        this.patients.set(updated);
        this.showEditDialog.set(false);
      },
      error: (err) => {
        console.error('❌ Error updating patient', err);
      }
    });
  }

  /**
   * Navigates to the patient's note management page.
   * @param patient The patient whose notes should be managed.
   */
  goToPatientNotes(patient: Patient) {
    this.router.navigate(['/admin/notes', patient.uid]);
  }

  /**
   * Navigates to a specified route.
   * @param path Route path to navigate to.
   */
  goTo(path: string) {
    this.router.navigate([`/${path}`]);
  }

  goToPage(page: number): void {
  if (page >= 0 && page < this.totalPages()) {
    this.currentPage.set(page);
    this.fetchPatients();
  }
}
}
