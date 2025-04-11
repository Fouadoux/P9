import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppUserResponse } from "../model/appUserResponse.model";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class appUserService{
    private apiUrl = 'http://localhost:8080/api/users/'; 
  
  constructor(private http: HttpClient) {}

  allUser(): Observable<AppUserResponse[]> {
    return this.http.get<AppUserResponse[]>(`${this.apiUrl}all`);
  }

  user(id: number): Observable<AppUserResponse> {
    return this.http.get<AppUserResponse>(`${this.apiUrl}${id}`);
  }
  
  updateUser(updateData: AppUserResponse): Observable<AppUserResponse> {
    return this.http.post<AppUserResponse>(`${this.apiUrl}update`, updateData);
  }
  
  toggleActiveUser(id: number): Observable<AppUserResponse> {
    return this.http.put<AppUserResponse>(`${this.apiUrl}toggle/${id}`, null);
  }


}