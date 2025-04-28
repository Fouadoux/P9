import { CommonModule } from '@angular/common';
import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Patient } from '../../model/patient.model';
import { MatDialog } from '@angular/material/dialog';
import { EditDialogComponent } from '../edit-dialog/edit-dialog.component';
import { Gender } from '../../model/gender.model';
import { PatientService } from '../../services/patient.service';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-patient-card',
  standalone: true,
  imports: [CommonModule,MatNativeDateModule],
  templateUrl: './patient-card.component.html',
  styleUrls: ['./patient-card.component.css']
})
export class PatientCardComponent {
  patient = input<Patient>();
  isEmpty = input<boolean>(false);

  dialog = inject(MatDialog);
  patientService = inject(PatientService);

  constructor(private router: Router) {}

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
        data: { data: newPatient, type: 'patient',title: 'Ajouter un patient' },
        width: '500px'
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          console.log('Nouveau patient à sauvegarder 🚀:', result);
          this.patientService.getAddPatient(result).subscribe({
            next: (createdPatient) => {
              console.log('✅ Patient ajouté avec succès :', createdPatient);
              this.router.navigate(['/patients', createdPatient.uid]);
            },
            error: (err) => {
              console.error('❌ Erreur lors de l\'ajout du patient :', err);
            },
            complete: () => {
              console.log('ℹ️ Requête d\'ajout de patient terminée.');
            }
          });
        }
      });
  }
}
