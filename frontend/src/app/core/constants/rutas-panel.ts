import { RolUsuario } from "../models/sesion.model";

/**
 * Home panel of each role. These routes arrive with the authentication user
 * story; each must exist before SesionService can return a real session,
 * otherwise the wildcard route would send the user back to the landing.
 */
export const RUTAS_PANEL: Readonly<Record<RolUsuario, string>> = {
  paciente: "/paciente",
  farmacia: "/farmacia",
  administrador: "/administrador",
};
