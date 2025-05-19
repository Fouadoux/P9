import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../model/note.model';

/**
 * Service responsible for handling all API interactions related to medical notes.
 * Allows creation, retrieval, update, and deletion of notes by patient or via admin actions.
 */
@Injectable({ providedIn: 'root' })
export class NoteService {

  /** Base URL for the note-related API endpoints */
  private apiUrl = 'http://localhost:8080/api/notes';

  /**
   * Injects Angular's HttpClient to perform HTTP requests.
   * @param http Angular HttpClient used for backend communication.
   */
  constructor(private http: HttpClient) {}

  /**
   * Retrieves all medical notes associated with a given patient ID.
   * @param patientId The UUID of the patient whose notes are being fetched.
   * @returns An Observable emitting an array of Note objects.
   */
  getNotesByPatientId(patientId: string): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  /**
   * Creates a new note for a specific patient.
   * @param data Object containing the patientId and the comment to add.
   * @returns An Observable emitting the created Note.
   */
  addNoteByPatientId(data: { patientId: string; comments: string }): Observable<Note> {
    return this.http.post<Note>(`${this.apiUrl}`, data);
  }

  /**
   * Updates an existing note.
   * This method is intended for normal users (not admin-level updates).
   * @param data The Note object with updated information.
   * @returns An Observable emitting the updated Note.
   */
  updateNote(data: Note): Observable<Note> {
    return this.http.put<Note>(`${this.apiUrl}`, data);
  }

  /**
   * Deletes a note. 
   * This method is for standard users and expects the full note object in the request body.
   * @param data The Note object to delete.
   * @returns An Observable emitting the deleted Note.
   */
  deleteNote(data: Note): Observable<Note> {
    return this.http.delete<Note>(`${this.apiUrl}`, { body: data });
  }

  /**
   * Admin-level note update.
   * Allows administrators to edit any note regardless of creation date or restrictions.
   * @param data The Note object to update.
   * @returns An Observable emitting the updated Note.
   */
  updateNoteAdmin(data: Note): Observable<Note> {
    return this.http.put<Note>(`${this.apiUrl}/admin/update`, data);
  }

  /**
   * Admin-level note deletion.
   * Allows administrators to delete any note.
   * @param note The Note object to delete.
   * @returns An Observable emitting the updated list of remaining notes.
   */
  deleteNoteAdmin(note: Note): Observable<Note[]> {
    return this.http.delete<Note[]>(`${this.apiUrl}/admin/delete`, { body: note });
  }
}
