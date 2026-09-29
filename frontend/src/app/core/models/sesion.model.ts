/**
 * Modelo de la sesión del usuario (capa core, modelos).
 *
 * Qué es: los roles del modelo de datos y la información mínima de una sesión.
 *
 * Para qué sirve: SesionService devuelve una Sesion y redirigirAutenticadoGuard
 * usa su rol para elegir el panel. Cada rol tiene su panel en la ruta "/<rol>".
 */
export type RolUsuario = "paciente" | "farmacia" | "administrador";

export interface Sesion {
  rol: RolUsuario;
}
