import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { Sesion, SesionService, rutaInicio } from "../services/sesion.service";

/**
 *
 * el guard de la landing: si el usuario ya inició sesión, lo manda a su panel
 * en vez de mostrarle la página pública
 *
 * cada rol tiene su ruta de inicio: /paciente, /farmacia o /administrador
 *
 */
export const redirigirAutenticadoGuard: CanActivateFn = (): boolean | UrlTree => {
  const sesion: Sesion | null = inject(SesionService).obtenerSesionActiva();

  if (!sesion) {
    return true;
  }

  return inject(Router).createUrlTree([rutaInicio(sesion.rol)]);
};
