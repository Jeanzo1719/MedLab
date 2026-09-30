import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { Sesion } from "../models/sesion.model";
import { SesionService } from "../services/sesion.service";

export const redirigirAutenticadoGuard: CanActivateFn = (): boolean | UrlTree => {
  const sesion: Sesion | null = inject(SesionService).obtenerSesionActiva();

  if (!sesion) {
    return true;
  }

  return inject(Router).createUrlTree(["/", sesion.rol]);
};
