import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { DiabetesRisk } from "../model/risk.model";

@Injectable({ providedIn: 'root' })
export class RiskService {
  private apiUrl = 'http://localhost:8080/api/risk'; 
  
  constructor(private http: HttpClient) {}

  getRiskLevelByPatientId(id: number): Observable<DiabetesRisk> {
    return this.http.get<DiabetesRisk>(`${this.apiUrl}/patient/${id}`);
  }
}