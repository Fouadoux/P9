import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

export const adminGuard: CanActivateFn = (route, state) => {
  const role = localStorage.getItem('userRole');
  return role === 'ADMIN';
};
