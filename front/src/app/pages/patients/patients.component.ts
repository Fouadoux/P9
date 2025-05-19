import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { SearchBarComponent } from '../../Components/search-bar/search-bar.component';
import { PatientService } from '../../services/patient.service';
import { PatientCardComponent } from '../../Components/patient-card/patient-card.component';
import { Patient } from '../../model/patient.model';
import { MatNativeDateModule } from '@angular/material/core';

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
    MatNativeDateModule
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

  /**
   * Computed signal returning a filtered list of patients based on the search query.
   * Matches against first name + last name (case insensitive).
   */
  filteredPatients = computed(() =>
    this.patients().filter(patient =>
      `${patient.firstName} ${patient.lastName}`.toLowerCase().includes(this.searchQuery().toLowerCase())
    )
  );

  /**
   * Constructor sets up an effect to fetch active patients when the component is initialized.
   * Updates signals based on the API response or error.
   */
  constructor() {
    effect(() => {
      this.patientService.getPatientsActive().subscribe({
        next: data => {
          this.patients.set(data);
          this.isLoading.set(false);
          console.log('patients:', this.patients());
        },
        error: error => {
          console.error('Erreur lors du chargement des patients', error);
          this.isLoading.set(false);
        }
      });
    });
  }

  /**
   * Updates the current search query signal based on user input.
   * @param query The new search query string.
   */
  onSearch(query: string) {
    this.searchQuery.set(query);
  }
}
