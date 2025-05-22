import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppUserResponse } from "../model/appUserResponse.model";
import { Observable } from "rxjs";
import { PaginatedResponse } from "../model/pagination.model";

/**
 * Service responsible for managing application users in GlucoVision.
 * Provides methods to interact with the backend API: fetching,
 * updating, and toggling user activation status.
 */
@Injectable({ providedIn: 'root' })
export class appUserService {

  /** Base URL for user-related API endpoints */
  private apiUrl = 'http://localhost:8080/api/users/'; 

  /**
   * Injects Angular's HttpClient to perform HTTP requests.
   * @param http The HttpClient service used for API calls.
   */
  constructor(private http: HttpClient) {}

  /**
   * Retrieves all users registered in the system.
   * @returns An Observable emitting a list of AppUserResponse objects.
   */
  allUser(): Observable<AppUserResponse[]> {
    return this.http.get<AppUserResponse[]>(`${this.apiUrl}all`);
  }

  /**
   * Fetches details of a specific user by their ID.
   * @param id The unique identifier of the user.
   * @returns An Observable emitting the user's details.
   */
  user(id: number): Observable<AppUserResponse> {
    return this.http.get<AppUserResponse>(`${this.apiUrl}${id}`);
  }

  /**
   * Updates user information such as name, email, role, etc.
   * @param updateData The user object containing updated fields.
   * @returns An Observable emitting the updated user object.
   */
  updateUser(updateData: AppUserResponse): Observable<AppUserResponse> {
    return this.http.post<AppUserResponse>(`${this.apiUrl}update`, updateData);
  }

  /**
   * Toggles the active status of a user account (enable/disable).
   * @param id The ID of the user whose status is being toggled.
   * @returns An Observable emitting the updated user object.
   */
  toggleActiveUser(id: number): Observable<AppUserResponse> {
    return this.http.put<AppUserResponse>(`${this.apiUrl}toggle/${id}`, null);
  }

  searchUsers(query: string, page: number, size: number): Observable<PaginatedResponse<AppUserResponse>> {
  return this.http.get<PaginatedResponse<AppUserResponse>>(`${this.apiUrl}search`, {
    params: {
      name: query,
      page: page.toString(),
      size: size.toString()
    }
  });
}

  allUserPaginated(page: number, size: number): Observable<PaginatedResponse<AppUserResponse>> {
    return this.http.get<PaginatedResponse<AppUserResponse>>(
      `${this.apiUrl}page?page=${page}&size=${size}`
    );
  }


}
