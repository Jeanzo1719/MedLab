import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { RolUsuario, Sesion, SesionService, rutaInicio } from "../services/sesion.service";

/**
 *
 * el guard de las vistas privadas: sin sesión manda a iniciar sesión, y con una
 * sesión de otro rol manda al panel que sí le corresponde
 *
 * se usa con los roles permitidos, por ejemplo requiereSesion("farmacia")
 *
 */
export function requiereSesion(...roles: RolUsuario[]): CanActivateFn {
  return (_ruta, estado): boolean | UrlTree => {
    const sesion: Sesion | null = inject(SesionService).obtenerSesionActiva();

    if (!sesion) {
      return inject(Router).createUrlTree(["/iniciar-sesion"], {
        queryParams: { volver: estado.url },
      });
    }

    if (roles.length > 0 && !roles.includes(sesion.rol)) {
      return inject(Router).createUrlTree([rutaInicio(sesion.rol)]);
    }

    return true;
  };
}
