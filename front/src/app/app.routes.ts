import { Routes } from '@angular/router';
import { PatientsComponent } from './pages/patients/patients.component';
import { PatientDetailsComponent } from './pages/patient-details/patient-details.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { RegisterSuccessComponent } from './pages/register-success/register-success.component';
import { authGuard } from './guards/auth.guard';
import { AdminUserManagementComponent } from './pages/admin-user-management/admin-user-management.component';
import { adminGuard } from './guards/admin.guard';
import { AccountDisabledComponent } from './pages/account-disabled/account-disabled.component';
import { AdminPatientsManagementComponent } from './pages/admin-patients-management/admin-patients-management.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register-success', component: RegisterSuccessComponent },
  { path: 'account-disabled', component: AccountDisabledComponent },



  { path: 'patients', component: PatientsComponent, canActivate: [authGuard] },
  { path: 'patients/:id', component: PatientDetailsComponent, canActivate: [authGuard] },

  { path: 'admin/users', component: AdminUserManagementComponent, canActivate: [adminGuard] },
  { path: 'admin/patients', component: AdminPatientsManagementComponent, canActivate: [adminGuard] }

];
