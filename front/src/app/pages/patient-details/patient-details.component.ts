import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../shared/components/patient-card/patient-card.component';

@Component({
  selector: 'app-patient-details',
  imports: [],
  templateUrl: './patient-details.component.html',
  styleUrl: './patient-details.component.css'
})
export class PatientDetailsComponent {
  private route=inject(ActivatedRoute);
  private patientService=inject(PatientService);
  private router=inject(Router);

  patient=signal<Patient | null>(null)

  constructor() {
    effect(() => {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.patientService.getPatientById(id).subscribe({
          next: (data) => this.patient.set(data),
          error: () => this.patient.set(null) 
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



}
