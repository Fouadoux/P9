import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPatientsManagementComponent } from './admin-patients-management.component';

describe('AdminPatientsManagementComponent', () => {
  let component: AdminPatientsManagementComponent;
  let fixture: ComponentFixture<AdminPatientsManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPatientsManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPatientsManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
