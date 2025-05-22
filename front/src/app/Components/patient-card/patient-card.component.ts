import { CommonModule } from '@angular/common';
import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Patient } from '../../model/patient.model';
import { MatDialog } from '@angular/material/dialog';
import { EditDialogComponent } from '../edit-dialog/edit-dialog.component';
import { Gender } from '../../model/gender.model';
import { PatientService } from '../../services/patient.service';
import { MatNativeDateModule } from '@angular/material/core';

/**
 * Component representing a single patient card.
 * 
 * Displays patient information and allows navigation to patient details.
 * If used in "empty" mode, opens a dialog to create a new patient.
 */
@Component({
  selector: 'app-patient-card',
  standalone: true,
  imports: [CommonModule, MatNativeDateModule],
  templateUrl: './patient-card.component.html',
  styleUrls: ['./patient-card.component.css']
})
export class PatientCardComponent {

  /** Patient data passed to the component */
  patient = input<Patient>();

  /** If true, renders the card as a placeholder for adding a new patient */
  isEmpty = input<boolean>(false);

  /** Injected Material dialog service */
  dialog = inject(MatDialog);

  /** Injected patient service for API operations */
  patientService = inject(PatientService);

  /**
   * Injected Angular Router used to navigate to patient details.
   * @param router Router instance for navigation
   */
  constructor(private router: Router) {}

  /**
   * Navigates to the patient's detail page if the card is populated.
   * If the card is marked as empty, triggers the patient creation dialog instead.
   */
  goToDetails() {
    if (this.isEmpty()) {
      this.addPatient(); 
      return;
    }

    const patient = this.patient();
    if (patient && patient.uid) {
      this.router.navigate(['/patients', patient.uid]);
    }
  }

  /**
   * Opens a dialog to create a new patient.
   * On successful creation, redirects to the newly created patient's detail page.
   */
  addPatient() {
    const newPatient: Partial<Patient> = {
      firstName: '',
      lastName: '',
      birthDate: null,
      gender: 'MALE' as Gender,
      address: '',
      phone: ''
    };

    this.dialog
      .open(EditDialogComponent, {
        data: {
          data: newPatient,
          type: 'patient',
          title: 'Ajouter un patient'
        },
        width: '500px'
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          console.log('🚀 New patient to save:', result);
          this.patientService.getAddPatient(result).subscribe({
            next: (createdPatient) => {
              console.log('✅ Patient successfully created:', createdPatient);
              this.router.navigate(['/patients', createdPatient.uid]);
            },
            error: (err) => {
              console.error('❌ Error while creating patient:', err);
            },
            complete: () => {
              console.log('ℹ️ Patient creation request completed.');
            }
          });
        }
      });
  }

  translateGender(gender: string | undefined | null): string {
  switch (gender) {
    case 'MALE': return 'Homme';
    case 'FEMALE': return 'Femme';
    default: return 'Non spécifié';
  }
}


}
