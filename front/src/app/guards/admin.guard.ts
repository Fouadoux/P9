import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

/**
 * Route guard used to restrict access to routes reserved for admin users.
 * 
 * Checks the `userRole` stored in `localStorage` and allows access
 * only if the role is `ADMIN`. Otherwise, redirects the user to a 404 page.
 * 
 * @returns `true` if the user is an admin, `false` with redirection otherwise.
 */
export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const role = localStorage.getItem('userRole');

  if (role === 'ADMIN') {
    return true;
  } else {
    router.navigate(['/404']);
    return false;
  }
};
