import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { AppHeaderComponent } from "./Components/app-header/app-header.component";
import { filter } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AppHeaderComponent,CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front';
  private router = inject(Router);
  private currentUrl = signal(this.router.url);

  showHeader = computed(() =>
    !this.currentUrl().startsWith('/login') && !this.currentUrl().startsWith('/register')
  );

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentUrl.set((event as NavigationEnd).urlAfterRedirects);
    });
  }
}
