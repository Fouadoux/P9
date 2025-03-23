import { Component, EventEmitter, model, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-search-bar',
  imports: [],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})

export class SearchBarComponent {
  search = output<string>(); // ✅ Utilisation correcte de output<string>()

  updateSearch(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.search.emit(inputElement.value); // ✅ Émet une string proprement
  }

}
