import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { DiabetesRisk } from "../model/risk.model";
import { LoginComponent } from "../pages/login/login.component";
import { Login } from "../model/login.model";
import { AuthResponse } from "../model/AuthResponse.model";

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth/'; 
  
  constructor(private http: HttpClient) {}

  login(loginData:Login): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}login`,loginData);
  }
}