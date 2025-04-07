import { Routes } from '@angular/router';
import { PatientsComponent } from './pages/patients/patients.component';
import { PatientDetailsComponent } from './pages/patient-details/patient-details.component';
import { EditPatientComponent } from './pages/edit-patient/edit-patient.component';
import { LoginComponent } from './pages/login/login.component'; // ⬅️ Ajout ici
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'patients', component: PatientsComponent },
  { path: 'patients/:id', component: PatientDetailsComponent },
  { path: 'patients/:id/edit', component: EditPatientComponent }
];
