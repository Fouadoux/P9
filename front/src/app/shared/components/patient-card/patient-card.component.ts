import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Router } from '@angular/router';


export interface Patient{
  id:string;
  firstName:string;
  lastName:string;
  birthDate:string;
  gender:string;
  address:string;
  phone:string;
}


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
