import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { Sesion } from "../models/sesion.model";
import { SesionService } from "../services/sesion.service";

/**
 * 
 * el guard de la landing: decide si se muestra la landing o si se manda al
 * usuario a su panel
 * 
 * antes de abrir la landing le pregunta a SesionService si hay sesión. Si no hay,
 * deja pasar (true). Si hay, lo manda a la ruta de su panel según su rol, por
 * ejemplo /paciente
 * 
 * por ahora siempre deja pasar, porque SesionService todavía devuelve null
 * 
 */
export const redirigirAutenticadoGuard: CanActivateFn = (): boolean | UrlTree => {
  const sesion: Sesion | null = inject(SesionService).obtenerSesionActiva();

  if (!sesion) {
    return true;
  }

  return inject(Router).createUrlTree(["/", sesion.rol]);
};
