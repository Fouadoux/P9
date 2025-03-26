import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientNoteComponent } from './patient-note.component';

describe('PatientNoteComponent', () => {
  let component: PatientNoteComponent;
  let fixture: ComponentFixture<PatientNoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientNoteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
