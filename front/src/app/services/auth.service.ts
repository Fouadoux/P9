import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Login } from "../model/login.model";
import { AuthResponse } from "../model/AuthResponse.model";

/**
 * Service responsible for handling authentication logic in the application.
 * Communicates with the backend API to perform login operations.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  /** Base URL for authentication-related endpoints */
  private apiUrl = 'http://localhost:8080/api/auth/'; 
  
  /**
   * Injects Angular's HttpClient to perform HTTP requests.
   * @param http The HttpClient service used for API communication.
   */
  constructor(private http: HttpClient) {}

  /**
   * Sends user credentials to the backend to perform login.
   * @param loginData Object containing user email and password.
   * @returns An Observable emitting an AuthResponse containing token and user details.
   */
  login(loginData: Login): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}login`, loginData);
  }
}
