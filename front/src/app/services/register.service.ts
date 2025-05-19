import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthResponse } from "../model/AuthResponse.model";
import { Register } from "../model/Register.model";

/**
 * Service responsible for user registration.
 * Sends user-provided registration data to the backend authentication API.
 */
@Injectable({ providedIn: 'root' })
export class RegisterService {

  /** Base URL for authentication-related endpoints */
  private apiUrl = 'http://localhost:8080/api/auth/'; 
  
  /**
   * Injects Angular's HttpClient for HTTP requests.
   * @param http The HttpClient instance used for communication with the backend.
   */
  constructor(private http: HttpClient) {}

  /**
   * Registers a new user account by sending registration data to the backend.
   * @param registerData The user-provided data required for registration (firstName, lastName, email, password, role).
   * @returns An Observable emitting an AuthResponse containing the token and user info if registration is successful.
   */
  register(registerData: Register): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}register`, registerData);
  }
}
