import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";

import { SesionService } from "../services/sesion.service";

/**
 * For public pages: visitors pass through, while users who already have a
 * session are sent to their role's panel. It never blocks a visitor, so the
 * page stays public.
 *
 * Each role's panel is the route with the role's name, declared in app.routes.ts
 * (/paciente, /farmacia, /administrador).
 */
export const redirigirAutenticadoGuard: CanActivateFn = () => {
  const sesion = inject(SesionService).obtenerSesionActiva();

  if (!sesion) {
    return true;
  }

  return inject(Router).createUrlTree(["/", sesion.rol]);
};
