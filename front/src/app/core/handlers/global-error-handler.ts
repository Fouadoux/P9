import { ErrorHandler, Injectable } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Global error handler that intercepts uncaught errors across the application.
 * 
 * - Redirects to a 404 page if the error has a status code of 404.
 * - Logs all other unhandled errors to the console.
 * - Optionally, you can redirect to a generic error page or add additional error handling logic.
 */
@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {

  /**
   * Injects Angular's Router to enable redirection from within the error handler.
   * @param router Angular Router for navigation.
   */
  constructor(private router: Router) {}

  /**
   * Method invoked automatically when an uncaught error occurs in the app.
   * @param error The error object thrown.
   */
  handleError(error: any): void {
    // Handle 404 errors specifically
    if (error.status === 404) {
      this.router.navigate(['/404']);
    }

    // Log the error to the console (extend this to log to remote services if needed)
    console.error('GlobalErrorHandler:', error);
  }
}
