import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormBuilderHelperService } from '../../services/form-builder-helper.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { OrderByFieldsPipe } from "../../pipe/orderByFields.pipe";


@Component({
  selector: 'app-edit-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatButtonModule],
  templateUrl: './edit-dialog.component.html',
  styleUrl: './edit-dialog.component.css'
})
export class EditDialogComponent<T extends Record<string, any>> {

  private formHelper = inject(FormBuilderHelperService);

  dialogData = inject(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<EditDialogComponent<T>>);

  form!: FormGroup;

  readonly roles = ['ADMIN', 'USER', 'PENDING'];
  readonly genders = ['MALE', 'FEMALE'];
  readonly hiddenFields = ['id', 'status', 'active','patientId', 'creationDate', 'modificationDate'];
  readonly fieldsOrder = this.formHelper.fieldsOrderMap[this.dialogData.type];


  ngOnInit() {
    this.form = this.formHelper.buildFormFromObject(this.dialogData.data, this.dialogData.type);  }

  isSelectField(key: string): boolean {
    return key === 'role' || key === 'gender';
  }

  getOptions(key: string): string[] {
    return key === 'role' ? this.roles : this.genders;
  }

  handleSave() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
  
    this.dialogRef.close(this.form.getRawValue());
  }

  handleClose() {
    this.dialogRef.close();
  }

 /* fieldsOrder = this.dialogData.type === 'patient'
  ? ['firstName', 'lastName', 'birthDate', 'gender', 'address', 'phone']
  : ['firstName', 'lastName', 'email', 'role'];
  */
}
