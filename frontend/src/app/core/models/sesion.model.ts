/** User roles from the data model; each one has its own panel at the route "/<rol>" */
export type RolUsuario = "paciente" | "farmacia" | "administrador";

export interface Sesion {
  rol: RolUsuario;
}
