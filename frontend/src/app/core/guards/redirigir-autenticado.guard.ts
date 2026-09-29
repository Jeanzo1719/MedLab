import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { Sesion } from "../models/sesion.model";
import { SesionService } from "../services/sesion.service";

/**
 * Guard de rutas públicas (capa core, guards).
 *
 * Qué es: una función CanActivateFn que el router ejecuta antes de entrar a una
 * página pública.
 *
 * Cómo funciona: pregunta a SesionService si hay una sesión activa. Sin sesión
 * deja pasar (true), así la página sigue siendo pública; con sesión devuelve un
 * UrlTree que redirige a "/<rol>", el panel del rol declarado en app.routes.ts
 * (/paciente, /farmacia, /administrador).
 *
 * Para qué sirve: cumple el criterio de la HU-10 de enviar a su panel al usuario
 * que ya inició sesión, sin bloquear nunca a un visitante.
 */
export const redirigirAutenticadoGuard: CanActivateFn = (): boolean | UrlTree => {
  const sesion: Sesion | null = inject(SesionService).obtenerSesionActiva();

  if (!sesion) {
    return true;
  }

  return inject(Router).createUrlTree(["/", sesion.rol]);
};
