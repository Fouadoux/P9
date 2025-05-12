import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

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
