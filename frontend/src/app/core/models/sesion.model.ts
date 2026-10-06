/**
 * 
 * los tipos de usuario que va a tener MedLab
 * 
 * solo acepta estos tres textos exactos; si en el código se escribe otro,
 * por ejemplo "admin", TypeScript marca el error
 * 
 */
export type RolUsuario = "paciente" | "farmacia" | "administrador";

/**
 * 
 * la forma de la sesión de un usuario que inició sesión
 * 
 * el login todavía no existe, así que por ahora solo la usan SesionService y
 * el guard de la landing, que siempre responden que no hay sesión
 * 
 */
export interface Sesion {
  /**
   * 
   * el tipo de usuario de la sesión. Sirve para saber a qué panel mandarlo:
   * /paciente, /farmacia o /administrador
   * 
   */
  rol: RolUsuario;
}
