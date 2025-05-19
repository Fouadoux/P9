import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormBuilderHelperService } from '../../services/form-builder-helper.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { provideNativeDateAdapter } from '@angular/material/core';

/**
 * Generic dialog component used to edit various data types (patients, users, notes).
 * 
 * Dynamically builds a form based on the provided `data` and `type`,
 * using the `FormBuilderHelperService`.
 * 
 * Supports select fields (e.g., role, gender) and filters out hidden/internal fields.
 * Emits form data on save and closes the dialog.
 */
@Component({
  selector: 'app-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,      
    MatDatepickerModule,
    MatNativeDateModule,     
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './edit-dialog.component.html',
  styleUrl: './edit-dialog.component.css',
  providers: [provideNativeDateAdapter()]
})
export class EditDialogComponent<T extends Record<string, any>> {

  /** Injected service for dynamically building forms */
  private formHelper = inject(FormBuilderHelperService);

  /** Data passed to the dialog: includes object data and type (e.g., 'user', 'patient') */
  dialogData = inject(MAT_DIALOG_DATA);

  /** Reference to the dialog instance to control close/save */
  dialogRef = inject(MatDialogRef<EditDialogComponent<T>>);

  /** Reactive form instance built dynamically from the input data */
  form!: FormGroup;

  /** Options for role and gender select fields */
  readonly roles = ['ADMIN', 'USER', 'PENDING'];
  readonly genders = ['MALE', 'FEMALE'];

  /** Fields that are not meant to be shown in the form */
  readonly hiddenFields = ['id', 'uid', 'status', 'active', 'patientId', 'creationDate', 'modificationDate'];

  /** Field display order determined by object type (from helper service) */
  readonly fieldsOrder = this.formHelper.fieldsOrderMap[this.dialogData.type];

  /**
   * Initializes the form dynamically using the FormBuilderHelperService.
   */
  ngOnInit() {
    console.log('EditDialogComponent loaded!');
    this.form = this.formHelper.buildFormFromObject(
      this.dialogData.data,
      this.dialogData.type
    );
  }

  /**
   * Determines whether a form field should be rendered as a select input.
   * @param key Field name to check.
   * @returns `true` if the field is a select input.
   */
  isSelectField(key: string): boolean {
    return key === 'role' || key === 'gender';
  }

  /**
   * Returns the available options for a given select field.
   * @param key Field name.
   * @returns Array of string options.
   */
  getOptions(key: string): string[] {
    return key === 'role' ? this.roles : this.genders;
  }

  /**
   * Handles form submission.
   * Emits the updated data and closes the dialog if the form is valid.
   */
  handleSave() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue());
  }

  /**
   * Closes the dialog without saving any changes.
   */
  handleClose() {
    this.dialogRef.close();
  }
}
