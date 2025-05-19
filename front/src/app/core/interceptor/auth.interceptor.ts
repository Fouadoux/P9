import { HttpInterceptorFn } from '@angular/common/http';

/**
 * HTTP interceptor that appends a JWT token to outgoing requests
 * except for authentication routes like login and register.
 *
 * Skipped paths:
 * - /auth/login
 * - /auth/register
 *
 * @param req The outgoing HTTP request.
 * @param next The next handler in the HTTP pipeline.
 * @returns A modified or original HTTP request passed to the next handler.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // Define the list of routes that should not have the token attached
  const excludedPaths = ['/auth/login', '/auth/register'];
  const shouldSkip = excludedPaths.some(path => req.url.includes(path));

  if (shouldSkip) {
    return next(req); // No token for login or register
  }

  if (token) {
    const modifiedReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(modifiedReq);
  }

  return next(req);
};
