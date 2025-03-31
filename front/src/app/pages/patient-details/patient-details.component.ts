import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../Components/patient-card/patient-card.component';
import { CommonModule } from '@angular/common';
import { PatientNoteComponent } from "../../Components/patient-note/patient-note.component";
import { DiabetesRisk } from '../../model/risk.model';
import { RiskService } from '../../services/risk.service';

@Component({
  selector: 'app-patient-details',
  imports: [CommonModule, PatientNoteComponent],
  templateUrl: './patient-details.component.html',
  styleUrl: './patient-details.component.css'
})
export class PatientDetailsComponent {
  private route=inject(ActivatedRoute);
  private patientService=inject(PatientService);
  private router=inject(Router);

  risk = signal<DiabetesRisk | null>(null);

  patient=signal<Patient | null>(null)

  patientId = signal<number | null>(Number(this.route.snapshot.paramMap.get('id')));
  private riskService = inject(RiskService);

  constructor() {
    effect(() => {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        const numId= Number(id)
        this.patientId.set(numId)
        this.patientService.getPatientById(id).subscribe({
          next: (data) => this.patient.set(data),
          error: () => this.patient.set(null) 
        });
      }
    });

    

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


    goBack() {
      this.router.navigate(['/patients']);
    }

    editPatient(){
      if(this.patient()){
        this.router.navigate(['/patients', this.patient()!.id, 'edit']);    
        }
    }

    refreshRisk() {
      const id = this.patientId();
      if (id !== null) {
        this.riskService.getRiskLevelByPatientId(id).subscribe({
          next: (data) => this.risk.set(data),
          error: () => this.risk.set(null),
        });
      }
    }

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
