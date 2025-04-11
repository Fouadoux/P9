import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Router } from '@angular/router';
import { Patient } from '../../model/patient.model';


@Component({
  selector: 'app-patient-card',
  imports: [CommonModule],
  templateUrl: './patient-card.component.html',
  styleUrl: './patient-card.component.css'
})


export class PatientCardComponent {
  patient=input<Patient>();

  constructor(private router: Router){}

  goToDetails(){
    if (this.patient() && this.patient()?.id) { 
    this.router.navigate(['/patients', this.patient()!.id]);
    } 
  }


}
