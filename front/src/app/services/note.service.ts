import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../model/note.model';

@Injectable({ providedIn: 'root' })
export class NoteService {
  private apiUrl = 'http://localhost:8080/api/notes'; // via le Gateway

  constructor(private http: HttpClient) {}

  getNotesByPatientId(patientId: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  addNoteByPatientId(data: { patientId: number; comments: string }): Observable<Note> {
    return this.http.post<Note>(`${this.apiUrl}`, data);
  }

  updateNote(data: Note): Observable<Note> {
    return this.http.put<Note>(
      `${this.apiUrl}?patientId=${data.patientId}&creationDate=${data.creationDate}&comments=${encodeURIComponent(data.comments)}`,
      null
    );
  }

  deleteNote(data: Note): Observable<Note> {
    return this.http.delete<Note>(
      `${this.apiUrl}?patientId=${data.patientId}&creationDate=${data.creationDate}`
    );
  }
  
  
  // Tu pourras ajouter addNote, updateNote, deleteNote plus tard
}
