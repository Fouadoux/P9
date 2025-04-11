import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'orderByFields',
  standalone: true
})
export class OrderByFieldsPipe implements PipeTransform {
  transform(obj: Record<string, any>, order: string[]): string[] {
    return order.filter(key => Object.keys(obj).includes(key));
  }
}
