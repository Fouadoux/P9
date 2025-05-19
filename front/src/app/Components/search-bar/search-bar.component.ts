import { Component, EventEmitter, output } from '@angular/core';

/**
 * Reusable search bar component.
 * 
 * Emits the search query string via an `@output()` signal whenever the input value changes.
 * Intended to be used with parent components that filter data dynamically.
 */
@Component({
  selector: 'app-search-bar',
  imports: [],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

  /** Output signal that emits the current search input value */
  search = output<string>();

  /**
   * Triggered whenever the user types in the input field.
   * Emits the current value through the `search` output signal.
   * 
   * @param event Input event triggered by the user.
   */
  updateSearch(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.search.emit(inputElement.value);
  }
}
