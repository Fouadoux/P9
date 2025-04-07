import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthResponse } from "../model/AuthResponse.model";
import { Register } from "../model/Register.model";

@Injectable({ providedIn: 'root' })
export class RegisterService {
  private apiUrl = 'http://localhost:8080/api/auth/'; 
  
  constructor(private http: HttpClient) {}

  register(registerData:Register): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}register`,registerData);
  }
}