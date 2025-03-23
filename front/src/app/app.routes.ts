import { Routes } from '@angular/router';
import { PatientsComponent } from './pages/patients/patients.component';
import { PatientDetailsComponent } from './pages/patient-details/patient-details.component';
import { EditPatientComponent } from './pages/edit-patient/edit-patient.component';

export const routes: Routes = [
    {path:'',redirectTo:'patients',pathMatch:'full'},
    {path:'patients',component:PatientsComponent},
    {path: 'patients/:id', component: PatientDetailsComponent },
    {path: 'patients/:id/edit',component: EditPatientComponent}
];
