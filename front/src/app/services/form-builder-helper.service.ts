import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class FormBuilderHelperService {

  private readonly fieldsOrderMap: Record<string, string[]> = {
    patient: ['firstName', 'lastName', 'birthDate', 'gender', 'address', 'phone'],
    user: ['firstName', 'lastName', 'email', 'role']
  };

  private readonly requiredFieldsMap: Record<string, string[]> = {
    patient: ['firstName', 'lastName', 'birthDate', 'gender'],
    user: ['firstName', 'lastName', 'email', 'role']
  };

  constructor(private fb: FormBuilder) {}

  buildFormFromObject<T extends Record<string, any>>(obj: T, type: 'patient' | 'user'): FormGroup {
    const group: Record<string, any> = {};
    const fieldsOrder = this.fieldsOrderMap[type];
    const requiredFields = this.requiredFieldsMap[type];
    const fieldsToHideInView  = ['id', 'status', 'active'];
  
    for (const key of Object.keys(obj)) {
        group[key] = requiredFields.includes(key)
          ? this.fb.control(obj[key], Validators.required)
          : this.fb.control(obj[key]);
      }
  
    return this.fb.group(group);
  }
}
