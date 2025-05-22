import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { SearchBarComponent } from '../../Components/search-bar/search-bar.component';
import { PatientService } from '../../services/patient.service';
import { PatientCardComponent } from '../../Components/patient-card/patient-card.component';
import { Patient } from '../../model/patient.model';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';

/**
 * Component responsible for displaying all active patients.
 * Includes a search bar and a filtered view of patient cards.
 *
 * Uses Angular signals for reactive state management and loading indicators.
 */
@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [
    CommonModule,
    SearchBarComponent,
    PatientCardComponent,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatButtonModule,MatIconModule,MatDialogModule
  ],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css'
})
export class PatientsComponent {

  /** Injected PatientService used to fetch patient data from backend */
  private patientService = inject(PatientService);

  /** Signal holding the list of all active patients */
  patients = signal<Patient[]>([]);

  /** Signal holding the current search query */
  searchQuery = signal<string>('');

  /** Signal indicating whether data is currently being loaded */
  isLoading = signal<boolean>(true);

currentPage = signal(0);
pageSize = signal(10);
totalPages = signal(0);

  /**
   * Constructor sets up an effect to fetch active patients when the component is initialized.
   * Updates signals based on the API response or error.
   */
  constructor() {
  effect(() => {
    this.fetchPatients();
  });
}

private fetchPatients(): void {
  this.isLoading.set(true);
  const name = this.searchQuery().trim();
  const page = this.currentPage();
  const size = this.pageSize();

  const request$ = name
    ? this.patientService.searchActivePatientsByName(name, page, size)
    : this.patientService.getActivePatientsPaginated(page, size);

  request$.subscribe({
    next: response => {
      this.patients.set(response.content);
      this.totalPages.set(response.totalPages);
      this.isLoading.set(false);
    },
    error: err => {
      console.error('Erreur lors du chargement des patients', err);
      this.isLoading.set(false);
    }
  });
}



 onSearch(query: string): void {
  this.searchQuery.set(query);
  this.currentPage.set(0); // reset pagination
  this.fetchPatients();
}

goToPage(page: number): void {
  if (page >= 0 && page < this.totalPages()) {
    this.currentPage.set(page);
    this.fetchPatients();
  }
}

}
