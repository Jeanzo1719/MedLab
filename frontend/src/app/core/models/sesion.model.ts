export type RolUsuario = "paciente" | "farmacia" | "administrador";

export interface Sesion {
  rol: RolUsuario;
}
