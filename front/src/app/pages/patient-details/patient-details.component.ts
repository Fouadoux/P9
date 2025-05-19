import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { CommonModule } from '@angular/common';
import { PatientNoteComponent } from '../../Components/patient-note/patient-note.component';
import { DiabetesRisk } from '../../model/risk.model';
import { RiskService } from '../../services/risk.service';
import { Patient } from '../../model/patient.model';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Validators } from '@angular/forms';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';

/**
 * Component responsible for displaying detailed information about a single patient,
 * including their personal data, medical notes, and diabetes risk evaluation.
 *
 * Also handles in-place editing of patient data via a modal dialog.
 */
@Component({
  selector: 'app-patient-details',
  imports: [CommonModule, PatientNoteComponent, MatDialogModule],
  templateUrl: './patient-details.component.html',
  styleUrl: './patient-details.component.css'
})
export class PatientDetailsComponent {

  /** Access to route parameters */
  private route = inject(ActivatedRoute);

  /** Service to fetch and update patient data */
  private patientService = inject(PatientService);

  /** Angular Router for navigation */
  private router = inject(Router);

  /** Signal holding the diabetes risk information */
  risk = signal<DiabetesRisk | null>(null);

  /** Signal holding the patient object */
  patient = signal<Patient | null>(null);

  /** Signal storing the patient ID from the route */
  patientId = signal<string | null>(this.route.snapshot.paramMap.get('id'));

  /** Service to retrieve diabetes risk information */
  private riskService = inject(RiskService);

  /** Material Dialog service to open the edit popup */
  private dialog = inject(MatDialog);

  /** Expose Angular Validators to the template if needed */
  validators = Validators;

  /**
   * Constructor sets up effects to load patient and risk data based on route ID.
   */
  constructor() {
    // Load patient details from route param
    effect(() => {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        const numId = String(id);
        this.patientId.set(numId);
        this.patientService.getPatientById(id).subscribe({
          next: (data) => this.patient.set(data),
          error: () => this.patient.set(null)
        });
      }
    });

    // Load risk evaluation for the patient
    effect(() => {
      const id = this.patientId();
      if (id !== null) {
        this.riskService.getRiskLevelByPatientId(id).subscribe({
          next: (data: DiabetesRisk | null) => this.risk.set(data),
          error: () => this.risk.set(null)
        });
      }
    });
  }

  /**
   * Navigates back to the patient list view.
   */
  goBack() {
    this.router.navigate(['/patients']);
  }

  /**
   * Opens a modal dialog to edit the patient's personal information.
   * @param patient The patient to be edited.
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
   * Sends the updated patient data to the backend and refreshes the local state.
   * Also re-evaluates the diabetes risk after update.
   * @param updatedPatient The updated patient object to save.
   */
  handlePatientSave(updatedPatient: Patient) {
    if (this.patient()) {
      this.patientService.updatePatient(updatedPatient).subscribe({
        next: (res) => {
          this.patient.set(res);
          this.refreshRisk();
        },
        error: (err) => {
          console.error('❌ Error updating patient', err);
        }
      });
    }
  }

  /**
   * Re-fetches the diabetes risk for the current patient.
   */
  refreshRisk() {
    const id = this.patientId();
    if (id !== null) {
      this.riskService.getRiskLevelByPatientId(id).subscribe({
        next: (data) => this.risk.set(data),
        error: () => this.risk.set(null),
      });
    }
  }

  /**
   * Converts a raw risk level string to a human-readable label.
   * @param risk The raw risk level (e.g., 'NONE', 'IN_DANGER').
   * @returns A user-friendly string label.
   */
  getRiskLabel(risk: string | null): string {
    switch (risk) {
      case 'NONE': return 'Aucun risque';
      case 'BORDERLINE': return 'Risque limité';
      case 'IN_DANGER': return 'Danger';
      case 'EARLY_ONSET': return 'Apparition précoce';
      default: return 'Inconnu';
    }
  }
}
