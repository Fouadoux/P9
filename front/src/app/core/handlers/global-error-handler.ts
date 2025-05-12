import { ErrorHandler, Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private router: Router) {}

  handleError(error: any): void {
    // Gérer spécifiquement les erreurs 404
    if (error.status === 404) {
      this.router.navigate(['/404']);
    }
    
    // Pour les autres erreurs, vous pouvez ajouter une logique supplémentaire
    console.error('GlobalErrorHandler:', error);
    
    // Optionnel : rediriger vers une page d'erreur générique
    // this.router.navigate(['/error']);
  }
}