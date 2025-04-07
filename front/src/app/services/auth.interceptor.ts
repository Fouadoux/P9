import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  //Ignorer les requêtes de login
  if (req.url.includes('/auth/login')) {
    return next(req);
  }

  //Ajouter le token si présent
  if (token) {
    const modifiedReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(modifiedReq);
  }

  //Pas de token, laisser passer la requête inchangée
  return next(req);
};
