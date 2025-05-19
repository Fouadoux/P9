import { Pipe, PipeTransform } from '@angular/core';

/**
 * Angular pipe used to reorder an object's keys based on a predefined field order.
 * This is especially useful for rendering dynamic forms or tables in a consistent order.
 *
 * Example usage in a template:
 * ```html
 * *ngFor="let key of someObject | orderByFields: ['firstName', 'lastName', 'email']"
 * ```
 */
@Pipe({
  name: 'orderByFields',
  standalone: true
})
export class OrderByFieldsPipe implements PipeTransform {

  /**
   * Filters and orders the keys of an object based on a predefined list of keys.
   * Only keys present in both the object and the provided order array will be returned, in the specified order.
   *
   * @param obj The object whose keys need to be ordered.
   * @param order An array defining the desired key order.
   * @returns An array of keys that exist in the object and match the specified order.
   */
  transform(obj: Record<string, any>, order: string[]): string[] {
    return order.filter(key => Object.keys(obj).includes(key));
  }
}
