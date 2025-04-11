import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Patient } from "../model/patient.model";

@Injectable({
    providedIn:'root'
})
export class PatientService{

    private apiUrl='http://localhost:8080/api/patients';

    
    private http = inject(HttpClient);
    
      getPatients(): Observable<Patient[]> {
        return this.http.get<Patient[]>(this.apiUrl);
      }

      getPatientsActive(): Observable<Patient[]> {
        return this.http.get<Patient[]>(`${this.apiUrl}/active`);
      }

      getPatientById(id:string):Observable<Patient>{
        return this.http.get<Patient>(`${this.apiUrl}/${id}`);
      }

      updatePatient(patient: Patient): Observable<Patient> {
        return this.http.put<Patient>(`${this.apiUrl}/${patient.id}`, patient);
      }

      toggleActivePatient(id: number): Observable<Patient> {
        return this.http.put<Patient>(`${this.apiUrl}/toggle/${id}`, null);
      }
      


}