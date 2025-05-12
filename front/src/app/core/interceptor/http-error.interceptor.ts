import { HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export function httpErrorInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isLoginRequest = req.url.includes('/auth/login');

      if (isLoginRequest) {
        // Laisse le composant gérer l'erreur de login
        return throwError(() => error);
      }

      if (error instanceof HttpErrorResponse) {
        switch (error.status) {
          case 401:
            router.navigate(['/login']);
            break;
          case 403:
            router.navigate(['/access-denied']);
            break;
          case 404:
            router.navigate(['/not-found']);
            break;
          case 500:
            router.navigate(['/server-error']);
            break;
        }
      }

      // ⛔️ Tu dois RETURN ici aussi, sinon rien ne sort de ton pipe
      return throwError(() => error);
    })
  );
}
