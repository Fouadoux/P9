import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoteModifDialogComponent } from './note-modif-dialog.component';

describe('NoteModifDialogComponent', () => {
  let component: NoteModifDialogComponent;
  let fixture: ComponentFixture<NoteModifDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoteModifDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoteModifDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
