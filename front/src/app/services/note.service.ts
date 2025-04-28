import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../model/note.model';

@Injectable({ providedIn: 'root' })
export class NoteService {
  private apiUrl = 'http://localhost:8080/api/notes';

  constructor(private http: HttpClient) {}

  getNotesByPatientId(patientId: string): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  addNoteByPatientId(data: { patientId: string; comments: string }): Observable<Note> {
    return this.http.post<Note>(`${this.apiUrl}`, data);
  }

  updateNote(data: Note): Observable<Note> {
    return this.http.put<Note>(
      `${this.apiUrl}`,
      data
    );
  }
  
  deleteNote(data: Note): Observable<Note> {
    return this.http.delete<Note>(
      `${this.apiUrl}`,
      { 
        body: data
      }
    );
  }

  updateNoteAdmin(data: Note): Observable<Note> {
    return this.http.put<Note>(
      `${this.apiUrl}/admin/update`,
      data
    );
  }

  deleteNoteAdmin(note: Note): Observable<Note[]> {
    return this.http.delete<Note[]>(
      `${this.apiUrl}/admin/delete`,
      { body: note }
    );
  }
  
}
