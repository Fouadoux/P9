import { Component, effect, inject, signal } from '@angular/core';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../model/patient.model';
import { AppCardComponent } from "../../Components/app-card/app-card.component";
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';



@Component({
  selector: 'app-admin-patients-management',
  imports: [CommonModule, AppCardComponent, MatDialogModule],
  templateUrl: './admin-patients-management.component.html',
  styleUrl: './admin-patients-management.component.css'
})
export class AdminPatientsManagementComponent {

  private patientService = inject(PatientService);
  patients = signal<Patient[]>([]);
  isLoading = signal<boolean>(true);
  public fb = inject(FormBuilder);
  selectedPatient = signal<Patient | null>(null);
  showEditDialog = signal(false);
  private dialog = inject(MatDialog);
  private router=inject(Router)

  validators = Validators;

  constructor() {
    effect(() => {
      this.patientService.getPatients().subscribe({
        next: (data) => {
          this.patients.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Erreur chargement patients', err);
          this.isLoading.set(false);
        }
      });
    });
  }

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
  

  handleToggleActive(patient: Patient) {
    this.patientService.toggleActivePatient(patient.uid).subscribe({
      next: (res) => {
        const patients = this.patients().map(p =>
          p.uid === res.uid ? res : p
        );
        this.patients.set(patients);
      },
      error: (err) => console.error('Erreur changement statut', err)
    });
  }

  handlePatientSave(updatedPatient: Patient) {
    this.patientService.updatePatient(updatedPatient).subscribe({
      next: (res) => {
        const patients = this.patients().map(p =>
          p.uid === res.uid ? res : p
        );
        this.patients.set(patients);
        this.showEditDialog.set(false);
      },
      error: (err) => {
        console.error('❌ Erreur mise à jour patient', err);
      }
    });
  }

  goToPatientNotes(patient: Patient) {
    this.router.navigate(['/admin/notes', patient.uid]);
  }
  
  goTo(path: string) {
    this.router.navigate([`/${path}`]);
  }
}
