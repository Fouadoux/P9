import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { DiabetesRisk } from "../model/risk.model";

/**
 * Service responsible for retrieving diabetes risk evaluations for patients.
 * Communicates with the backend risk-analysis API.
 */
@Injectable({ providedIn: 'root' })
export class RiskService {

  /** Base URL for diabetes risk analysis endpoints */
  private apiUrl = 'http://localhost:8080/api/risk'; 
  
  /**
   * Injects Angular's HttpClient to perform HTTP requests.
   * @param http Angular's HttpClient used to communicate with the backend.
   */
  constructor(private http: HttpClient) {}

  /**
   * Retrieves the diabetes risk level associated with a specific patient.
   * @param id The UUID of the patient whose risk level is to be evaluated.
   * @returns An Observable emitting a DiabetesRisk object representing the patient's risk level.
   */
  getRiskLevelByPatientId(id: string): Observable<DiabetesRisk> {
    return this.http.get<DiabetesRisk>(`${this.apiUrl}/patient/${id}`);
  }
}
