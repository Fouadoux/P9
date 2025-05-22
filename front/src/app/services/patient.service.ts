import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Patient } from "../model/patient.model";
import { PaginatedResponse } from "../model/pagination.model";

/**
 * Service responsible for managing patient-related operations.
 * Includes methods for retrieving, adding, updating, and toggling the activation state of patients.
 */
@Injectable({ providedIn: 'root' })
export class PatientService {

  /** Base URL for patient API endpoints */
  private apiUrl = 'http://localhost:8080/api/patients';

  /** Injects the Angular HttpClient via the inject() function */
  private http = inject(HttpClient);

  /**
   * Retrieves all patients from the backend (active and inactive).
   * @returns An Observable emitting an array of Patient objects.
   */
  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.apiUrl);
  }

  /**
   * Retrieves only the patients whose accounts are marked as active.
   * @returns An Observable emitting an array of active Patient objects.
   */
/*  getPatientsActive(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.apiUrl}/active`);
  }
    */

getActivePatientsPaginated(page: number, size: number): Observable<PaginatedResponse<Patient>> {
  return this.http.get<PaginatedResponse<Patient>>(`${this.apiUrl}/active/page?page=${page}&size=${size}`);
}


searchActivePatientsByName(name: string, page: number, size: number): Observable<PaginatedResponse<Patient>> {
  return this.http.get<PaginatedResponse<Patient>>(
    `${this.apiUrl}/active/search`, {
      params: {
        name,
        page: page.toString(),
        size: size.toString()
      }
    }
  );
}

searchPatientsByName(name: string, page: number, size: number): Observable<PaginatedResponse<Patient>> {
  return this.http.get<PaginatedResponse<Patient>>(`${this.apiUrl}/search`, {
    params: {
      name,
      page: page.toString(),
      size: size.toString()
    }
  });
}


getPatientsPaginated(page: number, size: number): Observable<PaginatedResponse<Patient>> {
  return this.http.get<PaginatedResponse<Patient>>(
    `${this.apiUrl}/page?page=${page}&size=${size}`
  );
}



  /**
   * Fetches a single patient by their unique identifier (UUID).
   * @param id The UUID of the patient.
   * @returns An Observable emitting the Patient object.
   */
  getPatientById(id: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.apiUrl}/${id}`);
  }

  /**
   * Updates an existing patient's data.
   * @param patient The Patient object containing updated data.
   * @returns An Observable emitting the updated Patient object.
   */
  updatePatient(patient: Patient): Observable<Patient> {
    return this.http.put<Patient>(`${this.apiUrl}/${patient.uid}`, patient);
  }

  /**
   * Toggles the active status of a patient (activate or deactivate).
   * @param id The UUID of the patient whose status is to be toggled.
   * @returns An Observable emitting the updated Patient object.
   */
  toggleActivePatient(id: string): Observable<Patient> {
    return this.http.put<Patient>(`${this.apiUrl}/toggle/${id}`, null);
  }

  /**
   * Adds a new patient to the database.
   * @param patient The Patient object containing the data to be added.
   * @returns An Observable emitting the newly created Patient object.
   */
  getAddPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(`${this.apiUrl}`, patient);
  }
}
