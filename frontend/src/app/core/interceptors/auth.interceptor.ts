import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, throwError } from "rxjs";

import { environment } from "../../../environments/environment";
import { SesionService } from "../services/sesion.service";

/**
 *
 * pone el token de la sesión en cada llamada a la API y reacciona si el backend
 * lo rechaza
 *
 * si el backend responde 401 (token vencido, sesión cerrada o alterada), borra
 * la sesión del navegador y manda al usuario a iniciar sesión otra vez. El
 * inicio de sesión se excluye para que un error de credenciales muestre su
 * mensaje en el formulario en vez de recargar la página
 *
 */
export const authInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const sesionService: SesionService = inject(SesionService);
  const router: Router = inject(Router);

  const token: string | null = sesionService.token();
  const esApi: boolean = peticion.url.startsWith(environment.apiUrl);
  const esInicioSesion: boolean = peticion.url === `${environment.apiUrl}/sesiones`;

  const conToken =
    token !== null && esApi
      ? peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : peticion;

  return siguiente(conToken).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && esApi && !esInicioSesion) {
        sesionService.limpiarSesionLocal();
        void router.navigate(["/iniciar-sesion"], { queryParams: { volver: router.url } });
      }
      return throwError(() => error);
    }),
  );
};
