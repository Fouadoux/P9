import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { SearchBarComponent } from '../../Components/search-bar/search-bar.component';
import { PatientService } from '../../services/patient.service';
import { PatientCardComponent } from '../../Components/patient-card/patient-card.component';
import { Patient } from '../../model/patient.model';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule,SearchBarComponent,PatientCardComponent,MatNativeDateModule ],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css'
})
export class PatientsComponent {
  private patientService = inject(PatientService);

  patients = signal<Patient[]>([]);
  searchQuery = signal<string>('');
  isLoading = signal<boolean>(true); 

  filteredPatients = computed(() =>
    this.patients().filter(patient =>
      `${patient.firstName} ${patient.lastName}`.toLowerCase().includes(this.searchQuery().toLowerCase())
    )
  );

  constructor() {
    effect(() => {
      this.patientService.getPatientsActive().subscribe(data => {
        this.patients.set(data);
        this.isLoading.set(false); // ✅ Cache le message "Chargement..."
            console.log('patients:', this.patients());

      }, error => {
        console.error('Erreur lors du chargement des patients', error);
        this.isLoading.set(false);
      });
    });
  }

  onSearch(query: string) {
    this.searchQuery.set(query);
  }
}
