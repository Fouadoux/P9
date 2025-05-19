import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

/**
 * Route guard that ensures the user is authenticated and has a valid role.
 *
 * - If no token is found in `localStorage`, the user is redirected to the login page.
 * - If the user's role is `PENDING`, they are redirected to the register success screen.
 * - Otherwise, access to the route is granted.
 *
 * @returns `true` if access is allowed; otherwise redirects and returns `false`.
 */
export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const role = localStorage.getItem('userRole');

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  if (role === 'PENDING') {
    router.navigate(['/register-success']);
    return false;
  }

  return true;
};
