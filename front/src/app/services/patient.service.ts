import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Patient } from "../Components/patient-card/patient-card.component";

@Injectable({
    providedIn:'root'
})
export class PatientService{

    private apiUrl='http://localhost:8080/api/patients';

    
    private http = inject(HttpClient);

   /* private getHeaders(): HttpHeaders {
        const username = 'admin'; // 🔥 Remplace avec ton utilisateur
        const password = 'password'; // 🔥 Remplace avec ton mot de passe
        const basicAuth = 'Basic ' + btoa(username + ':' + password);
    
        return new HttpHeaders({
          'Authorization': basicAuth
        });
      }
        */
    
      getPatients(): Observable<Patient[]> {
        return this.http.get<Patient[]>(this.apiUrl, { 
         // headers: this.getHeaders(), 
         // withCredentials: true // 🔥 Indispensable pour que CORS fonctionne avec l'auth
        });
      }
      getPatientById(id:string):Observable<Patient>{
        return this.http.get<Patient>(`${this.apiUrl}/${id}`);
      }

      updatePatient(patient: Patient): Observable<void> {
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': 'Basic ' + btoa('admin:motdepasse') // à adapter
        });
      
        return this.http.put<void>(`${this.apiUrl}/${patient.id}`, patient, { headers });
      }
      


}