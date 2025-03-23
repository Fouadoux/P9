import { CommonModule } from '@angular/common';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../shared/components/patient-card/patient-card.component';

@Component({
  selector: 'app-edit-patient',
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './edit-patient.component.html',
  styleUrl: './edit-patient.component.css'
})
export class EditPatientComponent {
  
  private fb= inject(FormBuilder);
  private route=inject(ActivatedRoute);
  private router=inject(Router);
  private patientService=inject(PatientService);

  form = signal<FormGroup | null>(null);
  loading=signal(true);

  patientId=signal<string|null>(this.route.snapshot.paramMap.get('id'));

  constructor() {
    effect(() => {
      const id = this.patientId();
      if (!id) {
        this.router.navigate(['/patients']);
        return;
      }

      this.patientService.getPatientById(id).subscribe({
        next: (patient) => {
          this.form.set(this.fb.group({
            firstName: [patient.firstName, Validators.required],
            lastName: [patient.lastName, Validators.required],
            birthDate: [patient.birthDate, Validators.required],
            gender: [patient.gender, Validators.required],
            address: [patient.address],
            phone: [patient.phone]
          }));
          this.loading.set(false);
        },
        error: () => {
          this.router.navigate(['/patients']);
        }
      });
    });
  }

  submit() {
    const f = this.form();
    if (!f || f.invalid || !this.patientId()) return;

    const updatedPatient: Patient = {
      id: this.patientId()!,
      ...f.value
    };

    this.patientService.updatePatient(updatedPatient).subscribe({
      next: () => this.router.navigate(['/patients', this.patientId()]),
      error: (err) => console.error('❌ Erreur lors de la mise à jour', err)
    });
  }

  cancel() {
    this.router.navigate(['/patients', this.patientId() ?? '']);
  }


}
