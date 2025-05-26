import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

/**
 * Utility service for dynamically generating Angular reactive forms
 * based on object types and required fields.
 *
 * This helper centralizes the logic for building forms for patients, users, and notes,
 * ensuring consistency in field order and required field validation.
 */
@Injectable({ providedIn: 'root' })
export class FormBuilderHelperService {

  /**
   * Defines the default field order for each form type.
   * Used to ensure consistent display across components.
   */
  public readonly fieldsOrderMap: Record<string, string[]> = {
    patient: ['firstName', 'lastName', 'birthDate', 'gender', 'address', 'phone'],
    user: ['firstName', 'lastName', 'email', 'role','password'],
    note: ['comments']
  };

  /**
   * Defines the required fields for each form type.
   * These fields will receive `Validators.required` in the generated form.
   */
  private readonly requiredFieldsMap: Record<string, string[]> = {
    patient: ['firstName', 'lastName', 'birthDate', 'gender'],
    user: ['firstName', 'lastName', 'email', 'role'],
    note: ['comments']
  };

  /**
   * Injects Angular's FormBuilder to construct reactive form controls.
   * @param fb Angular FormBuilder service.
   */
  constructor(private fb: FormBuilder) {}

  /**
   * Builds a FormGroup dynamically based on the provided object and type.
   * Automatically applies required validators according to the type configuration.
   *
   * @typeParam T The type of the input object.
   * @param obj An object representing initial form values.
   * @param type The type of form to build ('patient', 'user', or 'note').
   * @returns A fully initialized Angular FormGroup with validators.
   */
  buildFormFromObject<T extends Record<string, any>>(obj: T, type: 'patient' | 'user' | 'note'): FormGroup {
    const group: Record<string, any> = {};
    const fieldsOrder = this.fieldsOrderMap[type];
    const requiredFields = this.requiredFieldsMap[type];


for (const key of Object.keys(obj)) {
  const validators = [];

  if (requiredFields.includes(key)) {
    validators.push(Validators.required);
  }

 

  // Validation spécifique : numéro de téléphone = exactement 10 chiffres
  if (key === 'phone') {
    validators.push(Validators.pattern(/^\d{10}$/));
  }

  if (key === 'password') {
    validators.push(Validators.pattern(/^(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{9,}$/));
  }

  group[key] = this.fb.control(obj[key], validators);
  }

    return this.fb.group(group);
  }
}
