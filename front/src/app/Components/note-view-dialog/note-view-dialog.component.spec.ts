import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoteViewDialogComponent } from './note-view-dialog.component';

describe('NoteViewDialogComponent', () => {
  let component: NoteViewDialogComponent;
  let fixture: ComponentFixture<NoteViewDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoteViewDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoteViewDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
